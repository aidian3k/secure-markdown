package ee.pw.security.securemarkdown.domain.note.mapper;

import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import ee.pw.security.securemarkdown.domain.user.data.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoteDTOMapper {

	private final CurrentUserService currentUserService;

	public NoteDTO toDto(Note note) {
		return NoteDTO
			.builder()
			.id(note.getId())
			.isOwner(
				note.getOwner().getId().equals(currentUserService.getCurrentUserId())
			)
			.content(note.getContent())
			.updateTimeStamp(note.getUpdateTimeStamp())
			.title(note.getTitle())
			.ownerUsername(note.getOwner().getUsername())
			.noteVisibility(note.getNoteVisibility())
			.medias(note.getMedias())
			.build();
	}
}
