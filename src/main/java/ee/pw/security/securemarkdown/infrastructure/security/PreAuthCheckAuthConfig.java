package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PreAuthCheckAuthConfig {

	private static final int LOGIN_DELAY_TIME_IN_MS = 1000;
	private static final String EXCEEDED_NUMBER_OF_TRIES_MESSAGE =
		"Exceeded number of tries to login. Wait a moment!";
	private static final int MAXIMUM_NUMBER_OF_FAILED_LOGINS = 5;

	private final LoginAuditService loginAuditService;
	private final UserRepository userRepository;

	@Bean
	public UserDetailsChecker configureUserDetailsChecker() {
		return userDetails -> {
			handleAuthenticationDelay();
			final String userEmail = userDetails.getUsername();

			if (userRepository.findByEmail(userEmail).isEmpty()) {
				return;
			}

			// TODO - creating table for authorized user-agents handleUserLoginFromDifferentPlace(userEmail);

			List<LoginAudit> numberOfInvalidLoginTriesFromLastMinute = loginAuditService.getFailedLoginLogsFromLastMinute(
				userEmail
			);

			if (
				numberOfInvalidLoginTriesFromLastMinute.size() >=
				MAXIMUM_NUMBER_OF_FAILED_LOGINS
			) {
				throw new GenericAppException(EXCEEDED_NUMBER_OF_TRIES_MESSAGE);
			}
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
