package ee.pw.security.securemarkdown.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserRegistrationResponse {

	private String qrURI;
	private UserDTO userDTO;

	public static UserRegistrationResponse build(String qrURI, UserDTO userDTO) {
		return new UserRegistrationResponse(qrURI, userDTO);
	}
}
