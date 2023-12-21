package ee.pw.security.securemarkdown.domain.note.entity;

import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.validation.annotation.Password;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class Note {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notes_seq")
	@SequenceGenerator(name = "notes_seq", allocationSize = 1)
	private Long id;

	@Column(
		name = "title",
		length = ValidationConstants.FIVE_BITS,
		nullable = false
	)
	private String title;

	@Column(
		name = "content",
		length = ValidationConstants.TEN_BITS,
		nullable = false
	)
	private String content;

	@Column(name = "note_visibility", nullable = false)
	@Enumerated(EnumType.STRING)
	private NoteVisibility noteVisibility;

	@Password
	@Column(name = "note_password")
	private String notePassword = null;

	@ManyToOne(fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User owner;

	@CreationTimestamp
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDateTime creationTimeStamp;

	@UpdateTimestamp
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDateTime updateTimeStamp;
}
