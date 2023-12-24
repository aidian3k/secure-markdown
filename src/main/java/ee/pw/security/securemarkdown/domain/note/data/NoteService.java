package ee.pw.security.securemarkdown.domain.note.data;

import ee.pw.security.securemarkdown.domain.media.data.MediaService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import ee.pw.security.securemarkdown.domain.note.mapper.NoteDTOMapper;
import ee.pw.security.securemarkdown.domain.user.data.CurrentUserService;
import ee.pw.security.securemarkdown.domain.user.data.UserFacade;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import ee.pw.security.securemarkdown.infrastructure.exception.note.NoteNotFoundException;
import ee.pw.security.securemarkdown.infrastructure.security.EncryptionTools;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

	private final NoteRepository noteRepository;
	private final UserFacade userFacade;
	private final CurrentUserService currentUserService;
	private final PasswordEncoder passwordEncoder;
	private final MediaService mediaService;
	private final NoteDTOMapper noteDTOMapper;

	@Transactional
	public NoteDTO attachNoteToUser(NoteCreationDTO noteCreationDTO) {
		User currentUser = currentUserService.getCurrentUser();

		if (noteCreationDTO.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			Note securedNote = getSecuredNote(noteCreationDTO, currentUser);

			return saveNoteAndReturnNoteDTO(securedNote, currentUser);
		}

		Note note = makeNoteFromCreationDTO(noteCreationDTO, currentUser);

		return saveNoteAndReturnNoteDTO(note, currentUser);
	}

	private static Note makeNoteFromCreationDTO(
		NoteCreationDTO noteCreationDTO,
		User currentUser
	) {
		return Note
			.builder()
			.content(noteCreationDTO.getContent())
			.title(noteCreationDTO.getTitle())
			.noteVisibility(noteCreationDTO.getNoteVisibility())
			.owner(currentUser)
			.build();
	}

	private Note getSecuredNote(
		NoteCreationDTO noteCreationDTO,
		User currentUser
	) {
		return Note
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
	}

	public MainPageDTO getMainPageNotes() {
		User currentUser = currentUserService.getCurrentUser();

		return MainPageDTO
			.builder()
			.publicNotes(
				noteRepository
					.findAllByNoteVisibility(NoteVisibility.PUBLIC)
					.stream()
					.map(noteDTOMapper::toDto)
					.toList()
			)
			.privateNotes(
				noteRepository
					.findAllNotesByNoteVisibilityAndOwnerId(
						NoteVisibility.PRIVATE,
						currentUser.getId()
					)
					.stream()
					.map(noteDTOMapper::toDto)
					.toList()
			)
			.encryptedNotes(
				noteRepository
					.findAllNotesByNoteVisibilityAndOwnerId(
						NoteVisibility.ENCRYPTED,
						currentUser.getId()
					)
					.stream()
					.peek(note -> note.setContent("encrypted content"))
					.map(noteDTOMapper::toDto)
					.toList()
			)
			.build();
	}

	@Transactional
	public NoteDTO saveNoteAndReturnNoteDTO(Note noteToSave, User user) {
		Note savedNote = noteRepository.save(noteToSave);
		user.getNotes().add(savedNote);
		userFacade.saveUser(user);

		return NoteDTO
			.builder()
			.content(savedNote.getContent())
			.ownerUsername(savedNote.getOwner().getUsername())
			.title(savedNote.getTitle())
			.updateTimeStamp(LocalDateTime.now())
			.noteVisibility(savedNote.getNoteVisibility())
			.medias(savedNote.getMedias())
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

	public NoteDTO getNoteDTOByViewRequest(NoteViewDTO noteViewDTO, Long noteId) {
		Note note = getNoteById(noteId);

		if (note.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			return noteDTOMapper
				.toDto(note)
				.toBuilder()
				.content(
					EncryptionTools.decodeStringFromAESEncryption(
						note.getContent(),
						noteViewDTO.getPassword()
					)
				)
				.build();
		}

		return noteDTOMapper.toDto(note);
	}

	public NoteDTO getNoteDTOById(Long noteId) {
		return noteDTOMapper.toDto(getNoteById(noteId));
	}

	@Transactional
	public void deleteAttachedNote(Long noteId) {
		User currentUser = currentUserService.getCurrentUser();
		noteRepository.deleteById(noteId);
		currentUser.setNotes(
			currentUser
				.getNotes()
				.stream()
				.filter(note -> !note.getId().equals(noteId))
				.collect(Collectors.toSet())
		);
		userFacade.saveUser(currentUser);
	}

	public Boolean handleIsNoteEncrypted(Long noteId) {
		User currentUser = currentUserService.getCurrentUser();
		return currentUser
			.getNotes()
			.stream()
			.filter(note -> note.getId().equals(noteId))
			.findAny()
			.map(note -> note.getNoteVisibility().equals(NoteVisibility.ENCRYPTED))
			.orElseThrow(() ->
				new NoteNotFoundException("Note with id=[%d] not found")
			);
	}
}
