package ee.pw.security.securemarkdown.domain.user.data;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.util.Utils;
import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import ee.pw.security.securemarkdown.domain.user.dto.response.UserRegistrationResponse;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.domain.user.mapper.UserDTOMapper;
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
	) throws QrGenerationException {
		GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
		GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

		User user = User
			.builder()
			.email(userRegistrationRequest.getEmail())
			.username(userRegistrationRequest.getUsername())
			.password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
			.mfaSecret(googleAuthenticatorKey.getKey())
			.build();
		User savedUser = userRepository.save(user);
		QrData qrData = new QrData.Builder()
			.issuer("Secure-Markdown")
			.algorithm(HashingAlgorithm.SHA256)
			.digits(6)
			.period(30)
			.build();
		QrGenerator qrGenerator = new ZxingPngQrGenerator();

		return UserRegistrationResponse.build(
			Utils.getDataUriForImage(
				qrGenerator.generate(qrData),
				qrGenerator.getImageMimeType()
			),
			UserDTOMapper.toDto(savedUser)
		);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}
}
