package ee.pw.security.securemarkdown.infrastructure.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import ee.pw.security.securemarkdown.domain.user.entity.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationProvider extends DaoAuthenticationProvider {

	@Autowired
	public TokenAuthenticationProvider(UserDetailsService userDetailsService) {
		super.setUserDetailsService(userDetailsService);
	}

	@Override
	protected void additionalAuthenticationChecks(
		UserDetails userDetails,
		UsernamePasswordAuthenticationToken authentication
	) throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, authentication);

		TokenWebAuthenticationDetails authenticationDetails = (TokenWebAuthenticationDetails) authentication.getDetails();
		AppUserDetails user = (AppUserDetails) userDetails;
		String mfaToken = user.getMfaSecret();
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

		if (
			!googleAuthenticator.authorize(
				mfaToken,
				Integer.parseInt(authenticationDetails.getToken())
			)
		) {
			throw new BadCredentialsException(
				messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials",
					"Bad credentials"
				)
			);
		}
	}
}
