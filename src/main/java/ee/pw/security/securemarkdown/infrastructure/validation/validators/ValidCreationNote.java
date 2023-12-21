package ee.pw.security.securemarkdown.infrastructure.validation.validators;

import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.util.StringUtils;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
	validatedBy = ValidCreationNote.ValidCreationNoteValidator.class
)
public @interface ValidCreationNote {
	String message() default "Confirmation password does not match first password";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	class ValidCreationNoteValidator
		implements ConstraintValidator<ValidCreationNote, NoteCreationDTO> {

		@Override
		public void initialize(ValidCreationNote constraintAnnotation) {
			ConstraintValidator.super.initialize(constraintAnnotation);
		}

		@Override
		public boolean isValid(
			NoteCreationDTO creationDTO,
			ConstraintValidatorContext context
		) {
			boolean finalResult = true;

			if (
				creationDTO.getNoteVisibility().equals(NoteVisibility.ENCRYPTED) &&
				!StringUtils.hasText(creationDTO.getNotePassword())
			) {
				context
					.buildConstraintViolationWithTemplate(
						"Password cannot be null when note is encrypted!"
					)
					.addPropertyNode("password")
					.addConstraintViolation();
				finalResult = false;
			}

			if (
				creationDTO.getNoteVisibility().equals(NoteVisibility.ENCRYPTED) &&
				StringUtils.hasText(creationDTO.getNotePassword())
			) {
				PasswordValidator validator = new PasswordValidator(
					List.of(new LengthRule(8, 30))
				);
				RuleResult result = validator.validate(
					new PasswordData(creationDTO.getNotePassword())
				);

				if (!result.isValid()) {
					finalResult = finalResult & result.isValid();
					context
						.buildConstraintViolationWithTemplate(
							"Password for note is too week - consider using better password"
						)
						.addPropertyNode("notePassword")
						.addConstraintViolation();
				}
			}

			return finalResult;
		}
	}
}
