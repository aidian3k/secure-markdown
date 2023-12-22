package ee.pw.security.securemarkdown.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.security.securemarkdown.domain.note.data.NoteService;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteCreationDTO;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteEditRequest;
import ee.pw.security.securemarkdown.domain.note.dto.request.NoteViewDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.MainPageDTO;
import ee.pw.security.securemarkdown.domain.note.dto.response.NoteDTO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
