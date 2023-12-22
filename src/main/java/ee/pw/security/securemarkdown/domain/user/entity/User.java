package ee.pw.security.securemarkdown.domain.user.entity;

import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import ee.pw.security.securemarkdown.domain.note.entity.Note;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class User extends AppUserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
	@SequenceGenerator(name = "users_seq", allocationSize = 1)
	private Long id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "isUsingTwoFactorAuthentication", nullable = false)
	private boolean isUsingTwoFactorAuthentication = true;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "owner")
	@Builder.Default
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Note> notes = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	@Builder.Default
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<LoginAudit> loginAudits = new HashSet<>();

	@CreationTimestamp
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime creationTimeStamp;

	@UpdateTimestamp
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime updateTimeStamp;
}
