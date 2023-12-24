package ee.pw.security.securemarkdown.infrastructure.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry
					.addMapping("/api/**")
					.allowedOrigins("http://localhost:3000")
					.allowedMethods("GET", "POST", "PUT", "DELETE")
					.allowedHeaders("*");
			}
		};
	}

	@Bean
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
		configuration.setAllowedHeaders(List.of("Content-Type"));
		configuration.setMaxAge(3600L);

		source.registerCorsConfiguration("/api/**", configuration);

		return source;
	}
}
