package frauddetector.service;  

import frauddetector.enums.Category;  

import frauddetector.enums.Currency;  

import frauddetector.enums.Merchant;  

import frauddetector.model.Customer;  

import frauddetector.repository.CustomerRepository;  

import jakarta.annotation.PostConstruct;  

import org.slf4j.Logger;  

import org.slf4j.LoggerFactory;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;  

import java.util.Arrays;  

import java.util.List;  

@Service  

public class CustomerSeeder {  

    private static final Logger logger = LoggerFactory.getLogger(CustomerSeeder.class);  

    private final CustomerRepository customerRepository;  

    private final MongoTemplate mongoTemplate;  

    @Autowired  

    public CustomerSeeder(CustomerRepository customerRepository, MongoTemplate mongoTemplate) {  

        this.customerRepository = customerRepository;  

        this.mongoTemplate = mongoTemplate;  

    }  

    @PostConstruct  

    public void seedCustomers() {  

        if (mongoTemplate.collectionExists("customers") && customerRepository.count() > 0) {  

            logger.info("Customers already exist, skipping seeding");  

            return;  

        }  

        logger.info("Starting customer seeding process...");  

        // Create sample customers with different spending patterns  

        List<Customer> customers = Arrays.asList(  

            new Customer(  

                "user1",  

                Arrays.asList(Merchant.AMAZON, Merchant.WALMART),  

                Arrays.asList(Category.RETAIL, Category.TECH),  

                100.0,  

                20.0,  

                Currency.USD  

            ),  

            new Customer(  

                "user2",  

                Arrays.asList(Merchant.STARBUCKS, Merchant.MCDONALDS),  

                Arrays.asList(Category.FOOD, Category.BEVERAGES),  

                50.0,  

                10.0,  

                Currency.EUR  

            ),  

            new Customer(  

                "user3",  

                Arrays.asList(Merchant.APPLE, Merchant.MICROSOFT),  

                Arrays.asList(Category.TECH, Category.ELECTRONICS),  

                200.0,  

                50.0,  

                Currency.GBP  

            ),  

            new Customer(  

                "user4",  

                Arrays.asList(Merchant.TESCO, Merchant.LIDL),  

                Arrays.asList(Category.GROCERY, Category.FOOD),  

                75.0,  

                15.0,  

                Currency.EUR  

            )  

        );  

        customerRepository.saveAll(customers);  

        logger.info("Successfully seeded {} customers", customers.size());  

    }  

}