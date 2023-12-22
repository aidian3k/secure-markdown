package ee.pw.security.securemarkdown.application;

import ee.pw.security.securemarkdown.domain.note.data.NoteService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteEditRequest;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/note")
@Slf4j
class NoteController {

	private final NoteService noteService;

	@GetMapping(value = "/main-page", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MainPageDTO> handleMainPageNotesRequest() {
		return new ResponseEntity<>(noteService.getMainPageNotes(), HttpStatus.OK);
	}

	@PostMapping(
		value = "/save-note",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<NoteDTO> handleNewNoteRequest(
		@RequestBody @Valid NoteCreationDTO noteCreationDTO
	) {
		return new ResponseEntity<>(
			noteService.attachNoteToUser(noteCreationDTO),
			HttpStatus.CREATED
		);
	}

	@PutMapping
	@PreAuthorize("{@noteSecurityValidator.hasUserAccessToNote(#noteViewDTO)}")
	public ResponseEntity<NoteDTO> handleNoteEditRequest(
		@Valid NoteViewDTO noteViewDTO,
		@Valid NoteEditRequest noteEditRequest
	) {
		return new ResponseEntity<>(
			noteService.editAttachedNote(noteViewDTO, noteEditRequest),
			HttpStatus.OK
		);
	}

	@DeleteMapping
	@PreAuthorize("{@noteSecurityValidator.hasUserAccessToNote(#noteViewDTO)}")
	public ResponseEntity<Void> handleDeleteNoteRequest(NoteViewDTO noteViewDTO) {
		noteService.deleteAttachedNote(noteViewDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping
	@PreAuthorize("{@noteSecurityValidator.hasUserAccessToNote(#noteViewDTO)}")
	public ResponseEntity<NoteDTO> handleSingleNoteRequest(
		NoteViewDTO noteViewDTO
	) {
		return new ResponseEntity<>(
			noteService.getNoteDTOByViewRequest(noteViewDTO),
			HttpStatus.OK
		);
	}
}
