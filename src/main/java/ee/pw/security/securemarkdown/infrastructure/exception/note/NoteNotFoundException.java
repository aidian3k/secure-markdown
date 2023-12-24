package ee.pw.security.securemarkdown.infrastructure.exception.note;

import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoteNotFoundException extends RuntimeException {

	public NoteNotFoundException(String message) {
		super(message);
	}
}
