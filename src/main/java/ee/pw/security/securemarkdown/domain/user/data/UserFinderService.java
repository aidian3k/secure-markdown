package ee.pw.security.securemarkdown.domain.user.data;

import ee.pw.security.securemarkdown.domain.user.entity.User;
import ee.pw.security.securemarkdown.infrastructure.exception.user.UserNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFinderService {

	private final UserRepository userRepository;

	public User getUserByEmail(String email) {
		return userRepository
			.findByEmail(email)
			.orElseThrow(() ->
				new UserNotFoundException(
					String.format("User with email=[%s] has not been found!", email)
				)
			);
	}

	public Optional<User> getUserOptionalByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User getUserById(Long userId) {
		return userRepository
			.findById(userId)
			.orElseThrow(() ->
				new UserNotFoundException(
					String.format("User with id=[%d] has not been found", userId)
				)
			);
	}
}
