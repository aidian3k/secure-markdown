package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.loginaudit.data.LoginAuditService;
import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import ee.pw.security.securemarkdown.infrastructure.mail.MailService;
import jakarta.mail.MessagingException;
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
	private static final int MAXIMUM_NUMBER_OF_FAILED_LOGINS = 3;

	private final LoginAuditService loginAuditService;
	private final UserRepository userRepository;
	private final MailService mailService;

	@Bean
	public UserDetailsChecker configureUserDetailsChecker() {
		return userDetails -> {
			handleAuthenticationDelay();
			final String userEmail = ((User) userDetails).getEmail();

			List<LoginAudit> numberOfInvalidLoginTriesFromLastMinute = loginAuditService.getFailedLoginLogsFromLastMinute(
				userEmail
			);

			if (
				numberOfInvalidLoginTriesFromLastMinute.size() >=
				MAXIMUM_NUMBER_OF_FAILED_LOGINS
			) {
				try {
					mailService.sendEmail(
						userEmail,
						"There is many tries to login into your account",
						"There is many attempts of failed login on your account in the last minute. Consider changing password"
					);
				} catch (MessagingException exception) {
					throw new GenericAppException(EXCEEDED_NUMBER_OF_TRIES_MESSAGE);
				}

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
