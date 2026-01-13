package hr.fer.susjedi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SusjediApplication {

	public static void main(String[] args) {
		SpringApplication.run(SusjediApplication.class, args);
	}

}
