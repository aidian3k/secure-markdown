package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.user.data.UserFinderService;
import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
class AuthConfiguration {

	private final UserFinderService userFinderService;

	@Bean
	public PasswordEncoder configurePasswordEncoder() {
		return new BCryptPasswordEncoder(16);
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
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

		daoAuthenticationProvider.setUserDetailsService(
			configureUserDetailsService()
		);
		daoAuthenticationProvider.setHideUserNotFoundExceptions(true);
		daoAuthenticationProvider.setPasswordEncoder(configurePasswordEncoder());
		daoAuthenticationProvider.setPreAuthenticationChecks(authentication -> {
			final long timeOutDuringLoginSessionInMilliseconds = 500L;

			try {
				Thread.sleep(timeOutDuringLoginSessionInMilliseconds);
			} catch (InterruptedException interruptedException) {
				throw new IllegalStateException(
					"Interrupted exception was thrown during login"
				);
			}
		});

		return daoAuthenticationProvider;
	}

	@Bean
	public AuthenticationManager configureAuthManager(
		AuthenticationConfiguration authenticationConfiguration
	) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		CookieCsrfTokenRepository cookieCsrfTokenRepository = new CookieCsrfTokenRepository();
		cookieCsrfTokenRepository.setCookiePath("/");

		return cookieCsrfTokenRepository;
	}

//	@Bean
//	public AuthenticationFailureHandler configureAuthenticationFailureHandler() {
//		return null;
//	}
}
