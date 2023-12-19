package ee.pw.security.securemarkdown.domain.user.entity;

import ee.pw.security.securemarkdown.infrastructure.validation.annotation.Password;
import ee.pw.security.securemarkdown.infrastructure.validation.constants.ValidationConstants;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class AppUserDetails implements UserDetails {

	@Column(name = "email", length = ValidationConstants.EIGHT_BITS)
	@Email(regexp = ValidationConstants.EMAIL_PATTERN)
	private String email;

	@Column(name = "password", length = ValidationConstants.EIGHT_BITS)
	@Password
	private String password;

	@Column(name = "enabled")
	private boolean isEnabled = true;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}
}
