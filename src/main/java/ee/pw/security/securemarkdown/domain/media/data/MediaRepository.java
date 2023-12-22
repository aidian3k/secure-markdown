package ee.pw.security.securemarkdown.domain.media.data;

import ee.pw.security.securemarkdown.domain.media.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
interface MediaRepository extends JpaRepository<Media, Long> {
}
