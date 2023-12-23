package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
	securedEnabled = true,
	prePostEnabled = true,
	proxyTargetClass = true
)
public class SecurityConfiguration {

	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final CsrfTokenRepository csrfTokenRepository;
	private final LoginAuditService loginAuditService;

	@Bean
	public SecurityFilterChain configureSecurityFilterChain(
		HttpSecurity httpSecurity
	) throws Exception {
		httpSecurity.cors(Customizer.withDefaults());
		httpSecurity.httpBasic(Customizer.withDefaults());

		httpSecurity.csrf(httpSecurityCsrfConfigurer -> {
			//			httpSecurityCsrfConfigurer.ignoringRequestMatchers(
			//				"/api/auth/login",
			//				"/api/auth/create-user",
			//				"api/auth/reset-password"
			//			);
			//			httpSecurityCsrfConfigurer.csrfTokenRepository(csrfTokenRepository);
			httpSecurityCsrfConfigurer.disable();
		});

		httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
			httpSecurityFormLoginConfigurer.loginProcessingUrl("/api/auth/login");
			httpSecurityFormLoginConfigurer.authenticationDetailsSource(
				new WebAuthenticationDetailsSource()
			);
			httpSecurityFormLoginConfigurer.successHandler(
				(
					(request, response, authentication) -> {
						loginAuditService.saveLoginAuthenticationLog(request, false);
						response.setStatus(HttpStatus.OK.value());
					}
				)
			);
			httpSecurityFormLoginConfigurer.usernameParameter("email");
			httpSecurityFormLoginConfigurer.passwordParameter("password");
			httpSecurityFormLoginConfigurer.failureHandler(
				authenticationFailureHandler
			);
		});

		httpSecurity.logout(httpSecurityLogoutConfigurer -> {
			httpSecurityLogoutConfigurer.clearAuthentication(true);
			httpSecurityLogoutConfigurer.logoutUrl("/api/auth/logout").permitAll();
			httpSecurityLogoutConfigurer.deleteCookies("JSESSIONID");
			httpSecurityLogoutConfigurer.logoutSuccessHandler(
				(
					(request, response, authentication) -> {
						response.setStatus(200);
						SecurityContextHolder.clearContext();
					}
				)
			);
		});

		httpSecurity.authorizeHttpRequests(requestMatcherRegistry -> {
			requestMatcherRegistry.requestMatchers("/api/auth/login").permitAll();
			requestMatcherRegistry
				.requestMatchers("/api/auth/create-user")
				.permitAll();
		});

		httpSecurity.authorizeHttpRequests(requestMatcherRegistry ->
			requestMatcherRegistry
				.requestMatchers(
					"/api/auth/reset-password",
					"/api/auth/confirm-reset-password"
				)
				.permitAll()
		);

		httpSecurity.authorizeHttpRequests(requestMatcherRegistry ->
			requestMatcherRegistry.anyRequest().authenticated()
		);

		httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
			httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(
				(request, response, accessDeniedException) -> {
					new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				}
			)
		);

		return httpSecurity.build();
	}
}
