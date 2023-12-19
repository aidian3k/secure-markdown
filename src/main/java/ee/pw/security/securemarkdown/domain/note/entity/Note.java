package ee.pw.security.securemarkdown.domain.note.entity;

import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

	@Column(name = "is_public", nullable = false)
	private boolean isPublic;

	@ManyToOne(fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User owner;

	@CreationTimestamp
	private LocalDateTime creationTimeStamp;

	@UpdateTimestamp
	private LocalDateTime updateTimeStamp;
}
