package ee.pw.security.securemarkdown.domain.note.dto.request;

import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidCreationNote;
import ee.pw.security.securemarkdown.infrastructure.validation.validators.ValidPassword;
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
@ValidCreationNote
public class NoteCreationDTO {

	@NotBlank
	@Size(max = ValidationConstants.TEN_BITS)
	private String content;

	@NotBlank
	@Size(max = ValidationConstants.EIGHT_BITS)
	private String title;

	@NotNull
	private NoteVisibility noteVisibility;

	@ValidPassword
	String notePassword;
}
