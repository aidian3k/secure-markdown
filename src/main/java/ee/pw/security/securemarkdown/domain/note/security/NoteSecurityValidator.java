package ee.pw.security.securemarkdown.domain.note.security;

import ee.pw.security.securemarkdown.domain.note.data.NoteRepository;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import ee.pw.security.securemarkdown.domain.user.data.CurrentUserService;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class NoteSecurityValidator {

	private final CurrentUserService currentUserService;
	private final NoteRepository noteRepository;
	private final PasswordEncoder passwordEncoder;

	public boolean hasUserAccessToNote(NoteViewDTO noteViewRequest) {
		List<Note> notesThatUserHasAccessTo = noteRepository.getNotesUserHasAccessTo(
			currentUserService.getCurrentUserId()
		);
		Note foundNote = notesThatUserHasAccessTo
			.stream()
			.filter(note -> note.getId().equals(noteViewRequest.getNoteId()))
			.findAny()
			.orElseThrow(() ->
				new GenericAppException(
					String.format(
						"Access denied to note with id=[%d]",
						noteViewRequest.getNoteId()
					)
				)
			);

		if (foundNote.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			return validatePermissionToEncryptedNote(foundNote, noteViewRequest);
		}

		return true;
	}

	private boolean validatePermissionToEncryptedNote(
		Note foundNote,
		NoteViewDTO noteViewRequest
	) {
		if (!StringUtils.hasText(noteViewRequest.getPassword())) {
			throw new GenericAppException(
				String.format(
					"Access to note with id=[%d]",
					noteViewRequest.getNoteId()
				)
			);
		}

		return passwordEncoder.matches(
			noteViewRequest.getPassword(),
			foundNote.getNotePassword()
		);
	}
}
