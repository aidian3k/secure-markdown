package ee.pw.security.securemarkdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
public class SecureMarkdownApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureMarkdownApplication.class, args);
	}
}
