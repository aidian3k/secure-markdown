package ee.pw.security.securemarkdown.infrastructure.validation.validators;

import ee.pw.security.securemarkdown.infrastructure.security.SecurityTools;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;
import org.passay.AlphabeticalSequenceRule;
import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.NumericalSequenceRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.QwertySequenceRule;
import org.passay.RuleResult;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.WhitespaceRule;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPassword.ValidPasswordValidator.class)
public @interface ValidPassword {
	String message() default "Given password is invalid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	@Slf4j
	class ValidPasswordValidator
		implements ConstraintValidator<ValidPassword, String> {

		@Override
		public void initialize(ValidPassword constraintAnnotation) {
			ConstraintValidator.super.initialize(constraintAnnotation);
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			PasswordValidator validator = new PasswordValidator(
				Arrays.asList(
					new LengthRule(8, 30),
					new UppercaseCharacterRule(1),
					new DigitCharacterRule(1),
					new SpecialCharacterRule(1),
					new NumericalSequenceRule(3, false),
					new AlphabeticalSequenceRule(3, false),
					new QwertySequenceRule(3, false),
					new WhitespaceRule()
				)
			);
			RuleResult result = validator.validate(new PasswordData(value));

			if (result.isValid()) {
				log.debug(
					"Registering user with password with entropy=[{}]",
					SecurityTools.calculateEntropy(value)
				);

				return true;
			}

			context.disableDefaultConstraintViolation();
			context
				.buildConstraintViolationWithTemplate(
					String.join(",", validator.getMessages(result))
				)
				.addConstraintViolation();

			return false;
		}
	}
}
