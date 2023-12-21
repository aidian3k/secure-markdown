package ee.pw.security.securemarkdown.domain.loginaudit.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import ee.pw.security.securemarkdown.domain.loginaudit.enums.FailureReason;
import ee.pw.security.securemarkdown.domain.user.data.UserFinderService;
import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAuditService {

	private static final String LOCATION_PARAM = "User-Agent";

	private final LoginAuditRepository loginAuditRepository;
	private final UserRepository userRepository;
	private final UserFinderService userFinderService;

	public void saveLoginAuthenticationLog(
		HttpServletRequest request,
		boolean isFailure
	) {
		Either<Throwable, String> emailEither = getEmailParameterFromRequest(
			request
		);

		if (
			emailEither.isLeft() ||
			userRepository.findByEmail(emailEither.get()).isEmpty()
		) {
			return;
		}

		LoginAudit loginAudit = LoginAudit
			.builder()
			.failureReason(FailureReason.AUTHENTICATION)
			.location(request.getHeader(LOCATION_PARAM))
			.isSuccessful(isFailure)
			.ipAddress(request.getRemoteAddr())
			.user(userRepository.findByEmail(emailEither.get()).get())
			.build();

		loginAuditRepository.save(loginAudit);
	}

	public Either<Throwable, String> getEmailParameterFromRequest(
		HttpServletRequest request
	) {
		return Try
			.of(() -> {
				String requestPayload = request
					.getReader()
					.lines()
					.reduce("", (accumulator, actual) -> accumulator + actual);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode jsonNode = objectMapper.readTree(requestPayload);

				return jsonNode.get("email").asText();
			})
			.toEither();
	}

	public List<LoginAudit> getFailedLoginLogsFromLastMinute(String userEmail) {
		User user = userFinderService.getUserByEmail(userEmail);

		return loginAuditRepository.getFailedLoginLogsFromLastMinute(
			user.getId(),
			LocalDateTime.now().minusMinutes(1)
		);
	}
}
