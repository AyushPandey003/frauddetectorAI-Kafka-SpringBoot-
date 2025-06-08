package frauddetector.service;  
  
import frauddetector.model.Customer;  
import frauddetector.model.Transaction;  
import frauddetector.repository.CustomerRepository;  
import jakarta.annotation.PostConstruct;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.kafka.core.KafkaTemplate;  
import org.springframework.scheduling.annotation.Scheduled;  
import org.springframework.stereotype.Service;  
import java.util.List;  
import java.util.Random;  
  
@Service  
public class TransactionProducer {  
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);  
  
    private static final String TOPIC = "transactions";  
    private final EmbeddingGenerator embeddingGenerator;  
    private final KafkaTemplate<String, Transaction> kafkaTemplate;  
    private List<Customer> cachedCustomers;  
    private final Random random = new Random();  
    private final CustomerRepository customerRepository;  
  
    @Autowired  
    public TransactionProducer(KafkaTemplate<String, Transaction> kafkaTemplate, EmbeddingGenerator embeddingGenerator, CustomerRepository customerRepository) {  
        this.kafkaTemplate = kafkaTemplate;  
        this.embeddingGenerator = embeddingGenerator;  
        this.customerRepository = customerRepository;  
        refreshCustomerCache();  
    }
    @PostConstruct  
    public void loadCustomers() {  
        if (cachedCustomers.isEmpty()) {  
            logger.error("Warning: No customers found! Transactions may fail.");  
        } else {  
            logger.info("Cached {} customers for transaction generation.", cachedCustomers.size());  
        }  
    }
    @Scheduled(fixedRate = 5000) // Generate a transaction every 5 seconds
    public void generateAndSendTransaction() {  
        if (cachedCustomers.isEmpty()) {  
            logger.warn("No customers available for transaction generation");  
            return;  
        }  
        Customer randomCustomer = cachedCustomers.get(random.nextInt(cachedCustomers.size()));  
        Transaction transaction = Transaction.generateRandomTransaction(randomCustomer);  
        
        // Generate embedding for the transaction  
        float[] embedding = embeddingGenerator.generateEmbedding(transaction);  
        transaction.setEmbedding(embedding);  
  
        logger.info("Generated transaction: ID={}, Amount={}, Currency={}, Merchant={}, Category={}",  
            transaction.getTransactionId(),  
            transaction.getAmount(),  
            transaction.getCurrency(),  
            transaction.getMerchant(),  
            transaction.getCategory());  
  
        kafkaTemplate.send(TOPIC, transaction.getTransactionId(), transaction)  
            .whenComplete((result, ex) -> {  
                if (ex == null) {  
                    logger.info("Transaction sent successfully: {}", transaction.getTransactionId());  
                } else {  
                    logger.error("Failed to send transaction: {}", transaction.getTransactionId(), ex);  
                }  
            });  
    }  
  
    private void refreshCustomerCache() {  
        cachedCustomers = customerRepository.findAll();  
        logger.info("Cached {} customers for transaction generation", cachedCustomers.size());  
    }  
}