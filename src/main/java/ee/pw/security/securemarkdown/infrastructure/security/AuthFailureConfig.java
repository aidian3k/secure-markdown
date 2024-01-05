package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthFailureConfig {

	private final LoginAuditService loginAuditService;
	private static final int LOGIN_DELAY_TIME_IN_MS = 6000;
	private static final String EXCEEDED_NUMBER_OF_TRIES_MESSAGE =
		"Exceeded number of tries to login. Wait a moment!";

	@Bean
	public AuthenticationFailureHandler configureAuthenticationFailureHandler() {
		return (request, response, exception) -> {
			handleAuthenticationDelay();
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			if (!request.getServletPath().contains("/api/auth/login")) {
				return;
			}

			loginAuditService.saveLoginAuthenticationLog(request, true);
		};
	}

	private void handleAuthenticationDelay() {
		try {
			Thread.sleep(LOGIN_DELAY_TIME_IN_MS);
		} catch (InterruptedException interruptedException) {
			throw new GenericAppException(EXCEEDED_NUMBER_OF_TRIES_MESSAGE);
		}
	}
}
