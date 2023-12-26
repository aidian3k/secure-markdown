package ee.pw.security.securemarkdown.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@ToString
@Getter
@Setter
public class TokenWebAuthenticationDetails extends WebAuthenticationDetails {

	private final String token;

	public TokenWebAuthenticationDetails(HttpServletRequest request, String token) {
		super(request);
        this.token = token;
	}
}
