package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class SecurityConfiguration {

	private final AuthenticationFailureHandler authenticationFailureHandler;
	private final CsrfTokenRepository csrfTokenRepository;
	private final LoginAuditService loginAuditService;
	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain configureSecurityFilterChain(
		HttpSecurity httpSecurity
	) throws Exception {
		httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
		httpSecurity.cors(httpSecurityCorsConfigurer -> {
			httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource);
		});

		httpSecurity.csrf(httpSecurityCsrfConfigurer -> {
			CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
			requestHandler.setCsrfRequestAttributeName(null);

			httpSecurityCsrfConfigurer.ignoringRequestMatchers(
				"/api/auth/login",
				"api/auth/create-user",
				"api/auth/reset-password",
				"api/auth/confirm-rest-password",
				"api/auth/logout"
			);
			httpSecurityCsrfConfigurer.csrfTokenRepository(csrfTokenRepository);
			httpSecurityCsrfConfigurer.csrfTokenRequestHandler(requestHandler);
		});

		httpSecurity.formLogin(httpSecurityFormLoginConfigurer -> {
			httpSecurityFormLoginConfigurer.loginProcessingUrl("/api/auth/login");
			httpSecurityFormLoginConfigurer.authenticationDetailsSource(
				new TokenWebAuthenticationDetailsSource()
			);
			httpSecurityFormLoginConfigurer.successHandler(
				(
					(request, response, authentication) -> {
						loginAuditService.saveLoginAuthenticationLog(request, false);
						csrfTokenRepository.saveToken(
							csrfTokenRepository.generateToken(request),
							request,
							response
						);
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
			httpSecurityLogoutConfigurer.deleteCookies("XSRF-TOKEN");
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

		httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
			httpSecurityExceptionHandlingConfigurer
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					response.setStatus(HttpStatus.FORBIDDEN.value());
					new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
				})
				.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
				})
		);

		httpSecurity.sessionManagement(httpSecuritySessionManagementConfigurer ->
			httpSecuritySessionManagementConfigurer.sessionCreationPolicy(
				SessionCreationPolicy.ALWAYS
			)
		);

		httpSecurity.authorizeHttpRequests(requestMatcherRegistry ->
			requestMatcherRegistry.anyRequest().authenticated()
		);

		return httpSecurity.build();
	}
}
