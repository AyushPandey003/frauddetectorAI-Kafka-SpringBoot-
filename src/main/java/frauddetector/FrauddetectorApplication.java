package frauddetector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "frauddetector.repository")
@EnableScheduling
public class FrauddetectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FrauddetectorApplication.class, args);
	}

}
