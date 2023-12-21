package ee.pw.security.securemarkdown.domain.loginaudit.entity;

import ee.pw.security.securemarkdown.domain.loginaudit.enums.FailureReason;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

@Entity(name = "login_audit")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginAudit {

	@Id
	@GeneratedValue(
		strategy = GenerationType.SEQUENCE,
		generator = "login_audit_seq"
	)
	@SequenceGenerator(name = "login_audit_seq", allocationSize = 1)
	private Long id;

	@Column(name = "is_sucess", nullable = false)
	private boolean isSuccessful;

	@Column(name = "failure_reason")
	@Enumerated(EnumType.STRING)
	private FailureReason failureReason;

	@Column(name = "ip_address", length = ValidationConstants.FIVE_BITS)
	private String ipAddress;

	@Column(name = "location", length = ValidationConstants.TEN_BITS)
	private String location;

	@CreationTimestamp
	private LocalDateTime creationTimeStamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JoinColumn(name = "user_id")
	private User user;
}
