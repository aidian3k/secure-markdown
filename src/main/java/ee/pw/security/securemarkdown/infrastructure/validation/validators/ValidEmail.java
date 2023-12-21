package ee.pw.security.securemarkdown.infrastructure.validation.validators;

import ee.pw.security.securemarkdown.domain.user.data.UserRepository;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import lombok.RequiredArgsConstructor;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmail.RegisterEmailValidator.class)
public @interface ValidEmail {
	String message() default "User with such email already exist";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@RequiredArgsConstructor
	class RegisterEmailValidator
		implements ConstraintValidator<ValidEmail, String> {

		private final UserRepository userRepository;
		private String message;

		@Override
		public void initialize(ValidEmail constraintAnnotation) {
			this.message = constraintAnnotation.message();
			ConstraintValidator.super.initialize(constraintAnnotation);
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			boolean isUserEmailNotExist = userRepository.findByEmail(value).isEmpty();

			if (isUserEmailNotExist) {
				return true;
			}

			context
				.buildConstraintViolationWithTemplate(message)
				.addConstraintViolation();

			return false;
		}
	}
}
