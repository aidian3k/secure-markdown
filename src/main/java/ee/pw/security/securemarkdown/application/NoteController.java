package ee.pw.security.securemarkdown.application;

import ee.pw.security.securemarkdown.domain.note.data.NoteService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
@Slf4j
class NoteController {

	private final NoteService noteService;

	@GetMapping(value = "/encrypted")
	public ResponseEntity<List<NoteDTO>> handleEncryptedNotesRequest() {
		return new ResponseEntity<>(
			noteService.getMainPageNotes().encryptedNotes(),
			HttpStatus.OK
		);
	}

	@GetMapping(value = "/public")
	public ResponseEntity<List<NoteDTO>> handlePublicNotesRequest() {
		return new ResponseEntity<>(
			noteService.getMainPageNotes().publicNotes(),
			HttpStatus.OK
		);
	}

	@GetMapping(value = "/private")
	public ResponseEntity<List<NoteDTO>> handlePrivateNotesRequest() {
		return new ResponseEntity<>(
			noteService.getMainPageNotes().privateNotes(),
			HttpStatus.OK
		);
	}

	@PostMapping(
		value = "/save-note",
		produces = MediaType.APPLICATION_JSON_VALUE,
		consumes = "multipart/form-data"
	)
	public ResponseEntity<NoteDTO> handleNewNoteRequest(
		@RequestPart("noteCreationDTO") @Valid NoteCreationDTO noteCreationDTO,
		@RequestPart("medias") List<MultipartFile> medias
	) {
		return new ResponseEntity<>(
			noteService.attachNoteToUser(noteCreationDTO, medias),
			HttpStatus.CREATED
		);
	}

	@DeleteMapping("/{noteId}")
	@PreAuthorize(
		"{@noteSecurityValidator.hasUserAccessToNote(#noteViewDTO, #noteId)}"
	)
	public ResponseEntity<Void> handleDeleteNoteRequest(
		@PathVariable Long noteId,
		NoteViewDTO noteViewDTO
	) {
		noteService.deleteAttachedNote(noteId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{noteId}")
	@PreAuthorize(
		"{@noteSecurityValidator.hasUserAccessToNote(#noteViewDTO, #noteId)}"
	)
	public ResponseEntity<NoteDTO> handleSingleNoteRequest(
		@PathVariable Long noteId,
		NoteViewDTO noteViewDTO
	) {
		return new ResponseEntity<>(
			noteService.getNoteDTOByViewRequest(noteViewDTO, noteId),
			HttpStatus.OK
		);
	}
}
