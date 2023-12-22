package ee.pw.security.securemarkdown.domain.resetpassword;

import ee.pw.security.securemarkdown.infrastructure.validation.annotation.Password;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidConfirmationPassword;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ResetPasswordRequest {

	@NotBlank
	@NotNull
	String resetKey;

	@Password
	@ValidPassword
	private String password;

	private String confirmPassword;
}
