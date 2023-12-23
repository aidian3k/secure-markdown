package ee.pw.security.securemarkdown.domain.loginaudit.data;

import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {
	@Query(
		"select la from login_audit la where la.user.id = :userId and la.creationTimeStamp >= :creationDate and la.isSuccessful = false"
	)
	List<LoginAudit> getFailedLoginLogsFromLastMinute(
		Long userId,
		LocalDateTime creationDate
	);

	@Query(
		"select la from login_audit la where la.user.id=:userId and la.isSuccessful=true"
	)
	List<LoginAudit> getLoginAuditBySuccessfulIsTrue(Long userId);
}
