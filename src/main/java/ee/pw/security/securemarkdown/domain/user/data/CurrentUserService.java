package ee.pw.security.securemarkdown.domain.user.data;

import ee.pw.security.securemarkdown.domain.user.entity.User;
import java.nio.file.attribute.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserService {

	private final UserFinderService userFinderService;

	public User getCurrentUser() {
		return (User) SecurityContextHolder
			.getContext()
			.getAuthentication()
			.getPrincipal();
	}

	public Long getCurrentUserId() {
		return getCurrentUser().getId();
	}
}
