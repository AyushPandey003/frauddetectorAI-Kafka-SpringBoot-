package frauddetector.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.search.VectorSearchOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import static com.mongodb.client.model.search.SearchPath.fieldPath;

@Service
public class TransactionVectorSearchService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionVectorSearchService.class);

    private final MongoCollection<Document> transactionCollection;
    private static final String VECTOR_INDEX_NAME = "vector_index"; // Ensure this matches your Atlas index name
    private static final int SEARCH_LIMIT = 5; // Number of similar transactions to retrieve
    private static final int NUM_CANDIDATES = 50; // Number of approximate neighbors to consider

    @Autowired
    public TransactionVectorSearchService(MongoCollection<Document> transactionCollection) {
        this.transactionCollection = transactionCollection;
    }

    public boolean evaluateTransactionFraud(Document transaction) {
        // For demo purposes, implement a simple fraud detection logic
        // In a real system, this would use vector similarity search
        
        logger.info("Evaluating transaction {} for fraud", transaction.getString("transactionId"));
        
        // Get similar transactions
        List<Document> similarTransactions = findSimilarTransactions(transaction);
        logger.info("Found {} similar transactions", similarTransactions.size());
        
        // Simple fraud detection logic:
        // If more than 50% of similar transactions are marked as fraud,
        // mark this transaction as fraud
        long fraudCount = similarTransactions.stream()
                .filter(tx -> tx.getBoolean("isFraud", false))
                .count();
        
        boolean isFraud = (double) fraudCount / similarTransactions.size() > 0.5;
        logger.info("Transaction {} evaluated as {}", 
            transaction.getString("transactionId"), 
            isFraud ? "FRAUD" : "LEGITIMATE");
        
        return isFraud;
    }

    private List<Document> findSimilarTransactions(Document transaction) {
        // For demo purposes, return a small subset of transactions
        // In a real system, this would use vector similarity search
        return transactionCollection.find()
                .limit(5)
                .into(new java.util.ArrayList<>());
    }

    private void markTransactionAsFraud(String transactionId) {
        transactionCollection.updateOne(
                Filters.eq("transactionId", transactionId),
                Updates.set("isFraud", true)
        );
        logger.info("Transaction marked as fraud: {}", transactionId);
    }
}