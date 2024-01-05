package ee.pw.security.securemarkdown.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

	@Bean
	@Primary
	public CorsConfigurationSource configureCors() {
		CorsConfiguration configuration = new CorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		configuration.setAllowedOrigins(
			List.of(
				"https://localhost",
				"https://localhost/",
				"http://localhost:3000/",
				"http://localhost:3000"
			)
		);
		configuration.setAllowedMethods(
			List.of("GET", "POST", "DELETE", "PATCH", "OPTIONS")
		);
		configuration.setAllowedHeaders(
			List.of(
				"Content-Type",
				"Access-Control-Allow-Credentials",
				"x-xsrf-token",
				"X-CSRF-TOKEN"
			)
		);
		configuration.setMaxAge(3600L);
		configuration.setAllowCredentials(true);

		source.registerCorsConfiguration("/api/**", configuration);

		return source;
	}
}
