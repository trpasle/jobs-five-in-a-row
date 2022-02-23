package cz.ondrejguth.cz.jobs.piskvorky;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//interactively debug API client

@SpringBootApplication
public class FiveInARowApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(FiveInARowApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}
}
// https://piskvorky.jobs.cz/api/doc
