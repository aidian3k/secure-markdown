package ee.pw.security.securemarkdown.domain.user.data;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import ee.pw.security.securemarkdown.domain.user.dto.response.UserRegistrationResponse;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.domain.user.mapper.UserDTOMapper;
import ee.pw.security.securemarkdown.infrastructure.security.SecurityTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public UserRegistrationResponse registerUser(
		UserRegistrationRequest userRegistrationRequest
	) {
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
		GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

		User user = User
			.builder()
			.email(userRegistrationRequest.getEmail())
			.username(userRegistrationRequest.getUsername())
			.password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
			.mfaSecret(googleAuthenticatorKey.getKey())
			.enabled(true)
			.build();
		User savedUser = userRepository.save(user);
		String googleAuthenticatorQRGenerator = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
			"Secure-Markdown",
			savedUser.getEmail(),
			googleAuthenticatorKey
		);

		return UserRegistrationResponse.build(
			SecurityTools.generateQRCode(googleAuthenticatorQRGenerator),
			UserDTOMapper.toDto(savedUser)
		);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}
}
