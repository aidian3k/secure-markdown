package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
class AuthConfiguration {

	private final UserRepository userRepository;
	private final UserDetailsChecker userDetailsChecker;

	@Bean
	public PasswordEncoder configurePasswordEncoder() {
		final int strength = 16;

		return new BCryptPasswordEncoder(strength);
	}

	@Bean
	public AuthenticationManager configureAuthenticationManager() {
		return new ProviderManager(tokenAuthenticationProvider());
	}

	@Bean
	public TokenAuthenticationProvider tokenAuthenticationProvider() {
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
	public TokenWebAuthenticationDetailsSource totpAuthenticationDetailsSource() {
		return new TokenWebAuthenticationDetailsSource();
	}

	@Bean
	public SessionRegistry configureSessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public UserDetailsService configureUserDetailsService() {
		return email ->
			userRepository
				.findByEmail(email)
				.orElseThrow(() ->
					new BadCredentialsException("User witch such email not found")
				);
	}

	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		return CookieCsrfTokenRepository.withHttpOnlyFalse();
	}
}
