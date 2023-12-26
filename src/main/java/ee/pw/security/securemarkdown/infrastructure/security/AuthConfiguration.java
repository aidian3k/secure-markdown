package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.user.data.UserFinderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
class AuthConfiguration {

	private final UserFinderService userFinderService;
	private final UserDetailsChecker userDetailsChecker;

	@Bean
	public PasswordEncoder configurePasswordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SessionRegistry configureSessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public UserDetailsService configureUserDetailsService() {
		return userFinderService::getUserByEmail;
	}

	@Bean
	public AuthenticationProvider configureAuthenticationProvider() {
		TokenAuthenticationProvider tokenAuthenticationProvider = new TokenAuthenticationProvider();

		tokenAuthenticationProvider.setUserDetailsService(
			configureUserDetailsService()
		);
		tokenAuthenticationProvider.setHideUserNotFoundExceptions(true);
		tokenAuthenticationProvider.setPasswordEncoder(configurePasswordEncoder());
		tokenAuthenticationProvider.setPreAuthenticationChecks(userDetailsChecker);

		return tokenAuthenticationProvider;
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
		cookieCsrfTokenRepository.setHeaderName("X-XSRF-TOKEN");
		cookieCsrfTokenRepository.setCookieCustomizer(responseCookieBuilder -> {
			responseCookieBuilder.httpOnly(false);
			responseCookieBuilder.path("/");
		});

		return cookieCsrfTokenRepository;
	}
}
