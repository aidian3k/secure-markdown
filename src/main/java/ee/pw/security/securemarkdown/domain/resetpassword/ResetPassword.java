package ee.pw.security.securemarkdown.domain.resetpassword;

import ee.pw.security.securemarkdown.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResetPassword {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "reset_password_seq"
	)
	private Long id;

	@Column(name = "reset_password_random_key", nullable = false)
	private String resetPasswordRandomKey;

	@Column(name = "was_used", nullable = false)
	boolean wasUsed = false;

	@CreationTimestamp
	private LocalDateTime creationTimeStamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;
}
