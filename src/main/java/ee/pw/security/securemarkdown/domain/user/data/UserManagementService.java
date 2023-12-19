package ee.pw.security.securemarkdown.domain.user.data;

import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import ee.pw.security.securemarkdown.domain.user.dto.response.UserDTO;
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

	public UserDTO registerUser(UserRegistrationRequest userRegistrationRequest) {
		User user = User
			.builder()
			.email(userRegistrationRequest.getEmail())
			.username(userRegistrationRequest.getUsername())
			.password(passwordEncoder.encode(userRegistrationRequest.getPassword()))
			.isUsingTwoFactorAuthentication(
				userRegistrationRequest.isUsingTwoWayAuthentication()
			)
			.build();

		User savedUser = userRepository.save(user);

		return UserDTOMapper.toDto(savedUser);
	}
}
