package ee.pw.security.securemarkdown.domain.note.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NoteViewDTO {

	@NotNull
	@Positive
	private Long noteId;

	@NotBlank
	private String password;
}
