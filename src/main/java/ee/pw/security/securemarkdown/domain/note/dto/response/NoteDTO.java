package ee.pw.security.securemarkdown.domain.note.dto.response;

import ee.pw.security.securemarkdown.domain.media.entity.Media;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@Jacksonized
public class NoteDTO {

	private String title;
	private String content;
	private LocalDateTime updateTimeStamp;
	private String ownerUsername;
	private NoteVisibility noteVisibility;
	private Set<Media> medias;
}
