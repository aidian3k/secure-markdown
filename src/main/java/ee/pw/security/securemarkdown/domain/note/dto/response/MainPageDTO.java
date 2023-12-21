package ee.pw.security.securemarkdown.domain.note.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record MainPageDTO(
	List<NoteDTO> publicNotes,
	List<NoteDTO> encryptedNotes,
	List<NoteDTO> privateNotes
) {}
