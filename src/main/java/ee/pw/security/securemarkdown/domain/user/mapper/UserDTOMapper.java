package ee.pw.security.securemarkdown.domain.user.mapper;

import ee.pw.security.securemarkdown.domain.user.dto.response.UserDTO;
import ee.pw.security.securemarkdown.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDTOMapper {

	public static UserDTO toDto(User user) {
		return UserDTO
			.builder()
			.id(user.getId())
			.email(user.getEmail())
			.username(user.getUsername())
			.notes(user.getNotes())
			.build();
	}
}
