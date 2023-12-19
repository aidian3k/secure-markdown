package ee.pw.security.securemarkdown.domain.loginaudit.data;

import ee.pw.security.securemarkdown.domain.loginaudit.entity.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LoginAuditRepository extends JpaRepository<LoginAudit, Long> {}
