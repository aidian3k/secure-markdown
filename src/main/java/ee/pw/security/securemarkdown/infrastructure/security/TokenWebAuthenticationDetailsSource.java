package ee.pw.security.securemarkdown.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

class TokenWebAuthenticationDetailsSource
	extends WebAuthenticationDetailsSource {

	@Override
	public WebAuthenticationDetails buildDetails(HttpServletRequest request) {
		String token = request.getParameter("token");

		return new TokenWebAuthenticationDetails(request, token);
	}
}
