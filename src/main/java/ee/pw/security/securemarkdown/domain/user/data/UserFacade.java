package ee.pw.security.securemarkdown.domain.user.data;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserFacade {

	@Delegate
	private final UserManagementService userManagementService;
}
