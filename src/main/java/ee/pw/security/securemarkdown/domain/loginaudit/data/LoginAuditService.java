package ee.pw.security.securemarkdown.domain.loginaudit.data;


import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import ee.pw.security.securemarkdown.domain.loginaudit.enums.FailureReason;
import ee.pw.security.securemarkdown.domain.user.data.UserFinderService;
import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.mail.MailService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAuditService {

	private static final String LOCATION_PARAM = "User-Agent";

	private final LoginAuditRepository loginAuditRepository;
	private final UserRepository userRepository;
	private final UserFinderService userFinderService;
	private final MailService mailService;

	public void saveLoginAuthenticationLog(
		HttpServletRequest request,
		boolean isFailure
	) {
		Either<Throwable, String> emailEither = getEmailParameterFromRequest(
			request
		);

		if (emailEither.isLeft()) {
			return;
		}

		final String email = emailEither.get();
		Optional<User> optionalUser = userRepository.findByEmail(email);

		LoginAudit loginAudit = LoginAudit
			.builder()
			.failureReason(FailureReason.AUTHENTICATION)
			.location(request.getHeader(LOCATION_PARAM))
			.isSuccessful(!isFailure)
			.ipAddress(request.getRemoteAddr())
			.user(optionalUser.orElse(null))
			.build();

		if (optionalUser.isEmpty()) {
			return;
		}

		handleUserLoginFromDifferentLocation(request, emailEither.get());
		loginAuditRepository.save(loginAudit);
	}

	public Either<Throwable, String> getEmailParameterFromRequest(
		HttpServletRequest request
	) {
		return Try.of(() -> request.getParameterMap().get("email")[0]).toEither();
	}

	public void handleUserLoginFromDifferentLocation(
		HttpServletRequest request,
		String userEmail
	) {
		String location = request.getHeader(LOCATION_PARAM);
		List<LoginAudit> successfulLoginAttempts = getAllSuccessLoginAttempts(
			userEmail
		);
		if (CollectionUtils.isEmpty(successfulLoginAttempts)) {
			return;
		}

		Optional<LoginAudit> audit = successfulLoginAttempts
			.stream()
			.filter(loginAudit -> loginAudit.getLocation().equals(location))
			.findAny();

		if (audit.isPresent()) {
			return;
		}

		try {
			mailService.sendEmail(
				userEmail,
				"Attempt to login from different location",
				"There was an attempt to login from different location: " + location
			);
		} catch (MessagingException messagingException) {
			log.error("Error while trying to send the email");
		}
	}

	public List<LoginAudit> getFailedLoginLogsFromLastMinute(String userEmail) {
		User user = userFinderService.getUserByEmail(userEmail);

		return loginAuditRepository.getFailedLoginLogsFromLastMinute(
			user.getId(),
			LocalDateTime.now().minusMinutes(1)
		);
	}

	private List<LoginAudit> getAllSuccessLoginAttempts(String userEmail) {
		User user = userFinderService.getUserByEmail(userEmail);

		return loginAuditRepository.getLoginAuditBySuccessfulIsTrue(user.getId());
	}
}
