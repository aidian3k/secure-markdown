package ee.pw.security.securemarkdown.domain.note.dto.response;

import ee.pw.security.securemarkdown.domain.media.entity.Media;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Jacksonized
public class NoteDTO {

	private Long id;
	private String title;
	private String content;
	private LocalDateTime updateTimeStamp;
	private String ownerUsername;
	private NoteVisibility noteVisibility;
	private boolean isOwner;
	private Set<Media> medias;
}
