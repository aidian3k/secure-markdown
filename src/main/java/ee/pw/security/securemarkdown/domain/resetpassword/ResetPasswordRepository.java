package ee.pw.security.securemarkdown.domain.resetpassword;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
	Optional<ResetPassword> findResetPasswordByResetPasswordRandomKeyAndWasUsedIsFalse(
		String resetKey
	);
}
