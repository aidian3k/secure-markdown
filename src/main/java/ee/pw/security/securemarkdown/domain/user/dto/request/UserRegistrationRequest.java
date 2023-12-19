package ee.pw.security.securemarkdown.domain.user.dto.request;

import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidConfirmationPassword;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidEmail;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidPassword;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ValidConfirmationPassword
public class UserRegistrationRequest {

	@Size(max = ValidationConstants.EIGHT_BITS)
	@NotNull
	@NotBlank
	private String username;

	@Email(regexp = ValidationConstants.EMAIL_PATTERN)
	@ValidEmail
	@NotBlank
	private String email;

	@ValidPassword
	private String password;

	private String confirmationPassword;

	private boolean isUsingTwoWayAuthentication;
}
