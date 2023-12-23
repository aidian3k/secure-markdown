package ee.pw.security.securemarkdown.domain.user.dto.response;

import ee.pw.security.securemarkdown.domain.note.entity.Note;
import java.util.Set;
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
public class UserDTO {

	private Long id;
	private String username;
	private String email;
	private Set<Note> notes;
}
