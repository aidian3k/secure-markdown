package ee.pw.security.securemarkdown.domain.note.data;

import ee.pw.security.securemarkdown.domain.note.entity.Note;
import ee.pw.security.securemarkdown.domain.note.enums.NoteVisibility;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface NoteRepository extends JpaRepository<Note, Long> {
	@Query(
		"select n from notes n where n.noteVisibility=:noteVisibility and n.owner.id=:ownerId"
	)
	List<Note> findAllNotesByNoteVisibilityAndOwnerId(
		NoteVisibility noteVisibility,
		Long ownerId
	);

	List<Note> findAllByNoteVisibility(NoteVisibility noteVisibility);

	@Query(
		"SELECT n FROM notes n WHERE n.owner.id = :ownerId OR n.noteVisibility = NoteVisibility.PUBLIC"
	)
	List<Note> getNotesUserHasAccessTo(Long ownerId);
}
