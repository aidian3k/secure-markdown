package ee.pw.security.securemarkdown.domain.note.mapper;

import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageNoteDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MainPageNoteMapper {

	public MainPageNoteDTO toDto(Note note) {
		return MainPageNoteDTO
			.builder()
			.content(note.getContent())
			.updateTimeStamp(note.getUpdateTimeStamp())
			.title(note.getTitle())
			.ownerUsername(note.getOwner().getUsername())
			.noteVisibility(note.getNoteVisibility())
			.build();
	}
}
