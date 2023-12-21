package ee.pw.security.securemarkdown.domain.note.data;

import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import ee.pw.security.securemarkdown.domain.note.mapper.NoteDTOMapper;
import ee.pw.security.securemarkdown.domain.user.data.CurrentUserService;
import ee.pw.security.securemarkdown.domain.user.data.UserFacade;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import ee.pw.security.securemarkdown.infrastructure.security.EncryptionTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

	private final NoteRepository noteRepository;
	private final UserFacade userFacade;
	private final CurrentUserService currentUserService;
	private final PasswordEncoder passwordEncoder;

	public NoteDTO attachNoteToUser(NoteCreationDTO noteCreationDTO) {
		User currentUser = currentUserService.getCurrentUser();

		if (noteCreationDTO.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			Note securedNote = Note
				.builder()
				.content(
					EncryptionTools.encodeStringWithKeyWithAES(
						noteCreationDTO.getContent(),
						noteCreationDTO.getNotePassword()
					)
				)
				.title(noteCreationDTO.getTitle())
				.noteVisibility(NoteVisibility.ENCRYPTED)
				.notePassword(passwordEncoder.encode(noteCreationDTO.getNotePassword()))
				.owner(currentUser)
				.build();

			return saveNoteAndReturnNoteDTO(securedNote, currentUser);
		}

		Note note = Note
			.builder()
			.content(noteCreationDTO.getContent())
			.title(noteCreationDTO.getTitle())
			.noteVisibility(noteCreationDTO.getNoteVisibility())
			.owner(currentUser)
			.build();

		return saveNoteAndReturnNoteDTO(note, currentUser);
	}

	public MainPageDTO getMainPageNotes() {
		User currentUser = currentUserService.getCurrentUser();

		return MainPageDTO
			.builder()
			.publicNotes(
				noteRepository
					.findAllByNoteVisibility(NoteVisibility.PUBLIC)
					.stream()
					.map(NoteDTOMapper::toDto)
					.toList()
			)
			.privateNotes(
				noteRepository
					.findAllNotesByNoteVisibilityAndOwnerId(
						NoteVisibility.PRIVATE,
						currentUser.getId()
					)
					.stream()
					.map(NoteDTOMapper::toDto)
					.toList()
			)
			.encryptedNotes(
				noteRepository
					.findAllNotesByNoteVisibilityAndOwnerId(
						NoteVisibility.ENCRYPTED,
						currentUser.getId()
					)
					.stream()
					.map(NoteDTOMapper::toDto)
					.toList()
			)
			.build();
	}

	private NoteDTO saveNoteAndReturnNoteDTO(Note noteToSave, User user) {
		Note savedNote = noteRepository.save(noteToSave);
		user.getNotes().add(savedNote);
		userFacade.saveUser(user);

		return NoteDTO
			.builder()
			.content(savedNote.getContent())
			.title(savedNote.getTitle())
			.updateTimeStamp(savedNote.getUpdateTimeStamp())
			.build();
	}

	public Note getNoteById(Long noteId) {
		return noteRepository
			.findById(noteId)
			.orElseThrow(() ->
				new GenericAppException(
					String.format("Note with id=[%d] has not been found", noteId)
				)
			);
	}

	public NoteDTO getNoteDTOById(Long noteId) {
		return NoteDTOMapper.toDto(getNoteById(noteId));
	}
}
