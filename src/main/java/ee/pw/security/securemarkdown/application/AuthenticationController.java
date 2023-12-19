package ee.pw.security.securemarkdown.application;

import ee.pw.security.securemarkdown.domain.user.data.UserFacade;
import ee.pw.security.securemarkdown.domain.user.dto.request.UserRegistrationRequest;
import ee.pw.security.securemarkdown.domain.user.dto.response.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
class AuthenticationController {

	private final UserFacade userFacade;

	@PostMapping("/create-user")
	public ResponseEntity<UserDTO> registerUserToApplication(
		@RequestBody @Valid UserRegistrationRequest userRegistrationRequest
	) {
		return new ResponseEntity<>(
			userFacade.registerUser(userRegistrationRequest),
			HttpStatus.CREATED
		);
	}
}
