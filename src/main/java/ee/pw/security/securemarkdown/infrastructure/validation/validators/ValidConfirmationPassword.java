package ee.pw.security.securemarkdown.infrastructure.validation.validators;

import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
	validatedBy = ValidConfirmationPassword.ValidConfirmationPasswordValidator.class
)
public @interface ValidConfirmationPassword {
	String message() default "Confirmation password does not match first password";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class ValidConfirmationPasswordValidator
		implements
			ConstraintValidator<ValidConfirmationPassword, UserRegistrationRequest> {

		private String message;

		@Override
		public void initialize(ValidConfirmationPassword constraintAnnotation) {
			this.message = constraintAnnotation.message();
			ConstraintValidator.super.initialize(constraintAnnotation);
		}

		@Override
		public boolean isValid(
			UserRegistrationRequest value,
			ConstraintValidatorContext context
		) {
			boolean arePasswordsTheSame = value
				.getPassword()
				.equals(value.getConfirmationPassword());

			if (!arePasswordsTheSame) {
				context
					.buildConstraintViolationWithTemplate(message)
					.addPropertyNode("confirmationPassword")
					.addConstraintViolation();

				return false;
			}

			return true;
		}
	}
}
