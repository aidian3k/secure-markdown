package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthFailureConfig {

	private final LoginAuditService loginAuditService;

	@Bean
	public AuthenticationFailureHandler configureAuthenticationFailureHandler() {
		return (request, response, exception) -> {
			if (!request.getServletPath().contains("/api/auth/login")) {
				return;
			}

			loginAuditService.saveLoginAuthenticationLog(request, true);
		};
	}
}
