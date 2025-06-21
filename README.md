# Fraud Detection AI with Kafka and Spring Boot

A real-time fraud detection system that leverages AI-powered vector similarity search to identify suspicious transactions using Spring Boot, Apache Kafka, MongoDB, and OpenAI embeddings.

## 🚀 Features

- **Real-time Transaction Processing**: Stream-based processing using Apache Kafka
- **AI-Powered Fraud Detection**: Vector similarity search using OpenAI embeddings
- **MongoDB Integration**: Document-based storage with vector search capabilities
- **Automated Data Seeding**: Generate realistic transaction data for testing
- **Change Stream Monitoring**: Real-time database change monitoring
- **Scalable Architecture**: Microservices-ready design with event-driven patterns

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Transaction   │    │   Apache Kafka  │    │  Spring Boot    │
│   Producer      │───▶│   (Message      │───▶│   Consumer      │
│                 │    │    Broker)      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   OpenAI API    │    │   MongoDB       │    │   Vector Search │
│   (Embeddings)  │◀───│   (Document     │◀───│   Service       │
│                 │    │    Store)       │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

- **Java 21** - Modern Java with latest features
- **Spring Boot 3.2.3** - Application framework
- **Spring AI 0.8.1** - AI integration with OpenAI
- **Apache Kafka** - Message streaming platform
- **MongoDB** - Document database with vector search
- **Lombok** - Reduces boilerplate code
- **Jackson** - JSON processing

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MongoDB (with vector search capabilities)
- Apache Kafka
- OpenAI API key

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <repository-url>
cd frauddetectorAI-Kafka-SpringBoot-
```

### 2. Configure Environment

Create a `.env` file or set environment variables:

```bash
# MongoDB Configuration
MONGODB_URI=mongodb://localhost:27017/fraud

# OpenAI Configuration
OPENAI_API_KEY=your_openai_api_key_here

# Kafka Configuration (if using local Kafka)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 3. Update Application Properties

Edit `src/main/resources/application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=${MONGODB_URI}
spring.data.mongodb.database=fraud

# OpenAI
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.embedding.options.model=text-embedding-3-small

# Kafka
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS}
```

### 4. Start Dependencies

#### MongoDB (with Docker)
```bash
docker run -d --name mongodb -p 27017:27017 mongo:latest
```

#### Apache Kafka (with Docker)
```bash
# Start Zookeeper
docker run -d --name zookeeper -p 2181:2181 confluentinc/cp-zookeeper:latest

# Start Kafka
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```

### 5. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## 📊 Data Models

### Transaction
```java
{
  "id": "unique_id",
  "transactionId": "uuid",
  "userId": "user123",
  "amount": 150.00,
  "currency": "USD",
  "timestamp": 1640995200000,
  "merchant": "AMAZON",
  "category": "SHOPPING",
  "isFraud": false,
  "embedding": [0.1, 0.2, ...]
}
```

### Customer
```java
{
  "userId": "user123",
  "name": "John Doe",
  "email": "john@example.com",
  "preferredCurrency": "USD",
  "meanSpending": 100.0,
  "spendingStdDev": 25.0,
  "categories": ["SHOPPING", "FOOD", "TRANSPORT"]
}
```

## 🔧 Configuration

### MongoDB Vector Search Setup

1. Create a vector search index in MongoDB Atlas:
```javascript
{
  "mappings": {
    "dynamic": true,
    "fields": {
      "embedding": {
        "dimensions": 1536,
        "similarity": "cosine",
        "type": "knnVector"
      }
    }
  }
}
```

### Kafka Topics

The application expects the following Kafka topic:
- `transactions` - For incoming transaction data

## 🎯 Usage Examples

### 1. Generate Sample Data

The application includes data seeders that automatically generate realistic transaction data:

```java
// CustomerSeeder generates customer profiles
// TransactionSeeder generates transactions based on customer patterns
```

### 2. Send Test Transactions

```bash
# Using Kafka console producer
echo '{"userId":"user123","amount":150.0,"currency":"USD","merchant":"AMAZON","category":"SHOPPING"}' | \
kafka-console-producer --broker-list localhost:9092 --topic transactions
```

### 3. Monitor Fraud Detection

The application logs fraud detection results:
```
INFO  - Received transaction: abc123 - Amount: 150.0 USD - Merchant: AMAZON - Category: SHOPPING
INFO  - Transaction abc123 evaluated as LEGITIMATE
```

## 🔍 Fraud Detection Algorithm

The system uses a multi-layered approach:

1. **Vector Embedding Generation**: Convert transaction data to embeddings using OpenAI
2. **Similarity Search**: Find similar historical transactions using MongoDB vector search
3. **Pattern Analysis**: Analyze patterns in similar transactions
4. **Risk Scoring**: Calculate fraud probability based on similarity scores

### Suspicious Transaction Indicators

- Unusual spending amounts (significantly above customer's mean)
- Foreign currency transactions
- Unfrequent merchant categories
- Geographic anomalies
- Time-based patterns

## 📈 Monitoring and Observability

- **Application Logs**: Comprehensive logging with SLF4J
- **MongoDB Change Streams**: Real-time database change monitoring
- **Kafka Consumer Groups**: Scalable message processing
- **Vector Search Metrics**: Similarity search performance tracking

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify
```

## 📁 Project Structure

```
src/main/java/frauddetector/
├── config/                 # Configuration classes
│   ├── MongoDBConfig.java
│   └── OpenAIConfig.java
├── enums/                  # Enumeration classes
│   ├── Category.java
│   ├── Currency.java
│   └── Merchant.java
├── model/                  # Data models
│   ├── Customer.java
│   └── Transaction.java
├── repository/             # Data access layer
│   ├── CustomerRepository.java
│   └── TransactionRepository.java
├── service/                # Business logic
│   ├── CustomerSeeder.java
│   ├── EmbeddingGenerator.java
│   ├── TransactionChangeStreamListener.java
│   ├── TransactionConsumer.java
│   ├── TransactionProducer.java
│   ├── TransactionSeeder.java
│   └── TransactionVectorSearchService.java
└── FrauddetectorApplication.java
```

## 🔧 Development

### Adding New Features

1. **New Transaction Types**: Extend the `Transaction` model
2. **Custom Fraud Rules**: Modify `TransactionVectorSearchService`
3. **Additional Data Sources**: Create new consumers/producers
4. **Enhanced Embeddings**: Update `EmbeddingGenerator`

### Performance Optimization

- **Batch Processing**: Process transactions in batches
- **Caching**: Cache frequently accessed embeddings
- **Connection Pooling**: Optimize MongoDB and Kafka connections
- **Async Processing**: Use async methods for I/O operations

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the application logs for debugging information
- Review the MongoDB and Kafka documentation

## 🔮 Future Enhancements

- [ ] Machine Learning model integration
- [ ] Real-time dashboard
- [ ] Multi-tenant support
- [ ] Advanced analytics
- [ ] API rate limiting
- [ ] Enhanced security features
- [ ] Performance monitoring
- [ ] Automated scaling

---

**Note**: This is a demonstration project. For production use, ensure proper security measures, error handling, and monitoring are implemented. 