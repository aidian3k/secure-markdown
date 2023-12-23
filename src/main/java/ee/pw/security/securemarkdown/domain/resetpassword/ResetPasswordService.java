package ee.pw.security.securemarkdown.domain.resetpassword;

import ee.pw.security.securemarkdown.domain.user.data.UserFacade;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import ee.pw.security.securemarkdown.infrastructure.mail.MailService;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ServerConstants;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordService {

	private static final int RANDOM_KEY_LENGTH = 32;
	private static final int DURATION_OF_VALID_KEY_MINUTES = 30;

	private final ResetPasswordRepository resetPasswordRepository;
	private final MailService mailService;
	private final UserFacade userFacade;
	private final PasswordEncoder passwordEncoder;

	public void sendEmailWithLinkToResetPassword(String userEmail) {
		Optional<User> userOptional = userFacade.getUserOptionalByEmail(userEmail);

		if (userOptional.isEmpty()) {
			return;
		}

		User foundUser = userOptional.get();
		String generatedRandomKey = generateRandomString(RANDOM_KEY_LENGTH);

		ResetPassword resetPassword = ResetPassword
			.builder()
			.resetPasswordRandomKey(generatedRandomKey)
			.user(foundUser)
			.build();
		resetPasswordRepository.save(resetPassword);
		foundUser.getResetPasswords().add(resetPassword);
		userFacade.saveUser(foundUser);

		try {
			mailService.sendEmail(
				foundUser.getEmail(),
				"Reset password",
				"Reset password request was made to your account. " +
				"If you want to reset password go to website: " +
				ServerConstants.FRONTEND_SERVER_URL +
				"/reset-password?" +
				"randomKey=" +
				generatedRandomKey
			);
		} catch (MessagingException messagingException) {
			throw new GenericAppException(
				"There was an exception when sending email with reset password"
			);
		}
	}

	public Either<Throwable, Void> resetUserPassword(
		ResetPasswordRequest resetPasswordRequest
	) {
		return Try
			.run(() -> {
				ResetPassword resetPassword = resetPasswordRepository
					.findResetPasswordByResetPasswordRandomKeyAndWasUsedIsFalse(
						resetPasswordRequest.getResetKey()
					)
					.get();

				if (
					resetPassword
						.getCreationTimeStamp()
						.isAfter(
							LocalDateTime.now().minusMinutes(DURATION_OF_VALID_KEY_MINUTES)
						)
				) {
					throw new IllegalStateException(
						"Exception while trying to use reset password which is outdated"
					);
				}

				User user = resetPassword.getUser();
				user.setPassword(
					passwordEncoder.encode(resetPasswordRequest.getPassword())
				);
				resetPassword.setWasUsed(true);
				resetPasswordRepository.save(resetPassword);
				userFacade.saveUser(user);
			})
			.toEither();
	}

	public String generateRandomString(int keyLength) {
		SecureRandom random = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder(keyLength);
		final String CHARACTERS =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

		for (int i = 0; i < keyLength; i++) {
			int randomIndex = random.nextInt(CHARACTERS.length());
			char randomChar = CHARACTERS.charAt(randomIndex);
			stringBuilder.append(randomChar);
		}

		return stringBuilder.toString();
	}
}
