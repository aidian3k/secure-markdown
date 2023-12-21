package ee.pw.security.securemarkdown.infrastructure.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ErrorResponse {

	private String throwableName;
	private LocalDateTime timeStamp;
	private Long httpCode;
	private String message;
	private int errorCode;
}
