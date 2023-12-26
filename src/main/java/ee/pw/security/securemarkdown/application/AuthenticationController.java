package ee.pw.security.securemarkdown.application;

import dev.samstevens.totp.exceptions.QrGenerationException;
import ee.pw.security.securemarkdown.domain.resetpassword.ResetPasswordKeyRequest;
import ee.pw.security.securemarkdown.domain.resetpassword.ResetPasswordRequest;
import ee.pw.security.securemarkdown.domain.resetpassword.ResetPasswordService;
import ee.pw.security.securemarkdown.domain.user.data.UserFacade;
import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import ee.pw.security.securemarkdown.domain.user.dto.response.UserRegistrationResponse;
import io.vavr.control.Either;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
class AuthenticationController {

	private final UserFacade userFacade;
	private final ResetPasswordService resetPasswordService;

	@PostMapping("/create-user")
	public ResponseEntity<UserRegistrationResponse> registerUserToApplication(
		@RequestBody @Valid UserRegistrationRequest userRegistrationRequest
	) throws QrGenerationException {
		return new ResponseEntity<>(
			userFacade.registerUser(userRegistrationRequest),
			HttpStatus.CREATED
		);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Void> handleUserResetPasswordRequest(
		@RequestBody @Valid ResetPasswordKeyRequest resetPasswordKeyRequest
	) {
		resetPasswordService.sendEmailWithLinkToResetPassword(
			resetPasswordKeyRequest
		);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/confirm-reset-password")
	public ResponseEntity<Void> handleUserResetPasswordConfirmation(
		@RequestBody @Valid ResetPasswordRequest resetPasswordRequest
	) {
		Either<Throwable, Void> resetPasswordEither = resetPasswordService.resetUserPassword(
			resetPasswordRequest
		);

		if (resetPasswordEither.isLeft()) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
