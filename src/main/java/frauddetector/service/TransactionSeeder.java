package frauddetector.service;

import com.mongodb.client.MongoCollection;
import frauddetector.model.Customer;
import frauddetector.model.Transaction;
import frauddetector.repository.CustomerRepository;
import frauddetector.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class TransactionSeeder {
    private static final Logger logger = LoggerFactory.getLogger(TransactionSeeder.class);
    private final CustomerRepository customerRepository;
    private final EmbeddingGenerator embeddingGenerator;
    private final TransactionRepository transactionRepository;
    private final MongoCollection<Document> transactionCollection;

    @Autowired
    public TransactionSeeder(
            CustomerRepository customerRepository,
            EmbeddingGenerator embeddingGenerator,
            TransactionRepository transactionRepository,
            MongoCollection<Document> transactionCollection) {
        this.customerRepository = customerRepository;
        this.embeddingGenerator = embeddingGenerator;
        this.transactionRepository = transactionRepository;
        this.transactionCollection = transactionCollection;
    }

    @PostConstruct
    public void seedTransactions() {
        if (transactionCollection.countDocuments() > 0) {
            logger.info("Transactions already exist, skipping seeding");
            return;
        }

        logger.info("Starting transaction seeding process...");
        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            for (int i = 0; i < 5; i++) {
                Transaction transaction = Transaction.generateRandomTransaction(customer);
                transaction.setEmbedding(embeddingGenerator.generateEmbedding(transaction));
                transactionRepository.save(transaction);
                logger.info("Seeded transaction: {} - Amount: {} {} - Merchant: {} - Category: {}",
                        transaction.getTransactionId(),
                        transaction.getAmount(),
                        transaction.getCurrency(),
                        transaction.getMerchant(),
                        transaction.getCategory());
            }
        }

        logger.info("Successfully seeded {} transactions", customers.size() * 5);
    }
}