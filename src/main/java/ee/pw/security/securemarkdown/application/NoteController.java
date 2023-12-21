package ee.pw.security.securemarkdown.application;

import ee.pw.security.securemarkdown.domain.note.data.NoteService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/save")
	public ResponseEntity<NoteDTO> handleNewNoteRequest(
		@RequestBody @Valid NoteCreationDTO noteCreationDTO
	) {
		return new ResponseEntity<>(
			noteService.attachNoteToUser(noteCreationDTO),
			HttpStatus.CREATED
		);
	}

	@GetMapping
	@PreAuthorize("noteSecurityValidator.hasUserAccessToNote(noteViewDTO)")
	public ResponseEntity<NoteDTO> handleSingleNoteRequest(
		NoteViewDTO noteViewDTO
	) {
		return new ResponseEntity<>(
			noteService.getNoteDTOById(noteViewDTO.getNoteId()),
			HttpStatus.OK
		);
	}
}
