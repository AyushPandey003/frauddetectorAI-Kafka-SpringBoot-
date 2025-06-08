package frauddetector.service;  
  
import frauddetector.model.Transaction;  
import frauddetector.repository.TransactionRepository;  
import org.bson.Document;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.kafka.annotation.KafkaListener;  
import org.springframework.stereotype.Service;  
  
@Service  
public class TransactionConsumer {  
  
    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);  
  
    private final TransactionRepository transactionRepository;  
    private final TransactionVectorSearchService vectorSearchService;  
  
    @Autowired  
    public TransactionConsumer(TransactionRepository transactionRepository,  
                             TransactionVectorSearchService vectorSearchService) {  
        this.transactionRepository = transactionRepository;  
        this.vectorSearchService = vectorSearchService;  
    }  
  
    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")  
    public void consumeTransaction(Transaction transaction) {  
        logger.info("Received transaction: {} - Amount: {} {} - Merchant: {} - Category: {}",  
            transaction.getTransactionId(),  
            transaction.getAmount(),  
            transaction.getCurrency(),  
            transaction.getMerchant(),  
            transaction.getCategory());  
  
        // Save transaction to MongoDB  
        Transaction savedTransaction = transactionRepository.save(transaction);  
        logger.info("Saved transaction to MongoDB with ID: {}", savedTransaction.getId());  
  
        // Convert Transaction to Document for vector search  
        Document transactionDoc = new Document()  
            .append("transactionId", transaction.getTransactionId())  
            .append("userId", transaction.getUserId())  
            .append("amount", transaction.getAmount())  
            .append("currency", transaction.getCurrency().toString())  
            .append("timestamp", transaction.getTimestamp())  
            .append("merchant", transaction.getMerchant().toString())  
            .append("category", transaction.getCategory().toString())  
            .append("isFraud", transaction.isFraud())  
            .append("embedding", transaction.getEmbedding());  
  
        // Evaluate transaction for fraud  
        boolean isFraud = vectorSearchService.evaluateTransactionFraud(transactionDoc);  
        transaction.setFraud(isFraud);  
        transactionRepository.save(transaction);  
  
        logger.info("Transaction {} evaluated as {}", transaction.getTransactionId(), isFraud ? "FRAUD" : "LEGITIMATE");  
    }  
}