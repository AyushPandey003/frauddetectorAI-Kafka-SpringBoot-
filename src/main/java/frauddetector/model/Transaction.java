package frauddetector.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import frauddetector.enums.Category;
import frauddetector.enums.Currency;
import frauddetector.enums.Merchant;
import org.bson.BsonDocument;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import static frauddetector.enums.Merchant.getRandomMerchant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    @JsonProperty("transaction_id")
    private String transactionId;
    private String userId;
    private double amount;
    private Currency currency;
    private long timestamp;
    private Merchant merchant;
    private Category category;
    private boolean isFraud;
    private float[] embedding = {};

    public Transaction() {
        this.transactionId = UUID.randomUUID().toString();
        this.timestamp = Instant.now().toEpochMilli();
    }

    public Transaction(String userId, double amount, Currency currency, Merchant merchant, Category category) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.merchant = merchant;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isFraud() {
        return isFraud;
    }

    public void setFraud(boolean fraud) {
        isFraud = fraud;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public String generateEmbeddingText() {
        return String.format("%s %s %.2f %s %s %s %b", 
            transactionId, userId, amount, currency, merchant, category, isFraud);
    }

    public static Transaction generateRandomTransaction(Customer customer) {
        Random random = new Random();
        boolean isSuspicious = random.nextDouble() < 0.1; // 10% chance of suspicious transaction

        double amount;
        if (isSuspicious) {
            // Generate an amount significantly higher than the customer's mean spending
            amount = customer.getMeanSpending() + (random.nextDouble() * 5 * customer.getSpendingStdDev());
        } else {
            // Generate an amount around the customer's mean spending
            amount = customer.getMeanSpending() + (random.nextGaussian() * customer.getSpendingStdDev());
        }

        Currency currency = isSuspicious ? 
            Currency.getRandomSuspiciousCurrency(customer.getPreferredCurrency()) : 
            customer.getPreferredCurrency();

        Category category = isSuspicious ? 
            Category.getUnfrequentCategory(customer.getCategories()) : 
            Category.getFrequentCategory(customer.getCategories());

        Merchant merchant = Merchant.getRandomMerchant(category);

        return new Transaction(customer.getUserId(), amount, currency, merchant, category);
    }
}