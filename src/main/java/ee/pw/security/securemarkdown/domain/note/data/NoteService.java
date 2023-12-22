package ee.pw.security.securemarkdown.domain.note.data;

import ee.pw.security.securemarkdown.domain.media.data.MediaService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteEditRequest;
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
import ee.pw.security.securemarkdown.infrastructure.security.EncryptionTools;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

	private final NoteRepository noteRepository;
	private final UserFacade userFacade;
	private final CurrentUserService currentUserService;
	private final PasswordEncoder passwordEncoder;
	private final MediaService mediaService;

	@Transactional
	public NoteDTO attachNoteToUser(
		NoteCreationDTO noteCreationDTO,
		List<MultipartFile> multipartFileList
	) {
		User currentUser = currentUserService.getCurrentUser();

		if (noteCreationDTO.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			Note securedNote = getSecuredNote(noteCreationDTO, currentUser);

			return saveNoteAndReturnNoteDTO(
				securedNote,
				currentUser,
				multipartFileList
			);
		}

		Note note = makeNoteFromCreationDTO(noteCreationDTO, currentUser);

		return saveNoteAndReturnNoteDTO(note, currentUser, multipartFileList);
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

	@Transactional
	public NoteDTO saveNoteAndReturnNoteDTO(
		Note noteToSave,
		User user,
		List<MultipartFile> multipartFileList
	) {
		mediaService.attachMediasToPhotos(multipartFileList, noteToSave.getId());

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

	public NoteDTO getNoteDTOByViewRequest(NoteViewDTO noteViewDTO) {
		Note note = getNoteById(noteViewDTO.getNoteId());

		if (note.getNoteVisibility().equals(NoteVisibility.ENCRYPTED)) {
			return NoteDTOMapper
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

		return NoteDTOMapper.toDto(note);
	}

	public NoteDTO getNoteDTOById(Long noteId) {
		return NoteDTOMapper.toDto(getNoteById(noteId));
	}

	private Note makeNoteFromEditDto(
		NoteViewDTO noteViewDTO,
		NoteEditRequest noteEditRequest
	) {
		return Note
			.builder()
			.id(noteViewDTO.getNoteId())
			.content(noteEditRequest.getContent())
			.title(noteEditRequest.getTitle())
			.noteVisibility(noteEditRequest.getNoteVisibility())
			.owner(currentUserService.getCurrentUser())
			.notePassword(noteEditRequest.getNotePassword())
			.build();
	}

	@Transactional
	public void deleteAttachedNote(NoteViewDTO noteViewDTO) {
		User currentUser = currentUserService.getCurrentUser();
		noteRepository.deleteById(noteViewDTO.getNoteId());
		currentUser.setNotes(
			currentUser
				.getNotes()
				.stream()
				.filter(note -> !note.getId().equals(noteViewDTO.getNoteId()))
				.collect(Collectors.toSet())
		);
		userFacade.saveUser(currentUser);
	}
}
