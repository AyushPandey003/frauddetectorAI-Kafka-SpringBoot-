package frauddetector.service;

import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import frauddetector.model.Transaction;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionChangeStreamListener {
    private static final Logger logger = LoggerFactory.getLogger(TransactionChangeStreamListener.class);
    private final MongoCollection<Document> transactionCollection;
    private final TransactionVectorSearchService vectorSearchService;
    private final ExecutorService executorService;
    private volatile boolean running = true;
    private MongoCursor<ChangeStreamDocument<Document>> cursor;

    @Autowired
    public TransactionChangeStreamListener(
            MongoCollection<Document> transactionCollection,
            TransactionVectorSearchService vectorSearchService) {
        this.transactionCollection = transactionCollection;
        this.vectorSearchService = vectorSearchService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @PostConstruct
    public void startListening() {
        logger.info("Starting transaction change stream listener");
        executorService.submit(this::listenForChanges);
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down transaction change stream listener");
        running = false;
        
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                logger.warn("Error closing MongoDB cursor", e);
            }
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void listenForChanges() {
        try {
            ChangeStreamIterable<Document> changeStream = transactionCollection.watch();
            cursor = changeStream.iterator();

            while (running && cursor.hasNext()) {
                try {
                    ChangeStreamDocument<Document> change = cursor.next();
                    if (change.getOperationType() == com.mongodb.client.model.changestream.OperationType.INSERT) {
                        Document fullDocument = change.getFullDocument();
                        if (fullDocument != null) {
                            boolean isFraud = vectorSearchService.evaluateTransactionFraud(fullDocument);
                            logger.info("Transaction {} evaluated as {}", 
                                fullDocument.getString("transactionId"), 
                                isFraud ? "FRAUD" : "LEGITIMATE");
                        }
                    }
                } catch (Exception e) {
                    if (running) {
                        logger.error("Error processing change stream document", e);
                    }
                }
            }
        } catch (Exception e) {
            if (running) {
                logger.error("Error in change stream listener", e);
            }
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    logger.warn("Error closing MongoDB cursor in finally block", e);
                }
            }
        }
    }
}