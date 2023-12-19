package ee.pw.security.securemarkdown.infrastructure.validation.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationConstants {

	public static final int FIVE_BITS = 32;
	public static final int SIX_BITS = 64;
	public static final int SEVEN_BITS = 128;
	public static final int EIGHT_BITS = 256;
	public static final int NINE_BITS = 512;
	public static final int TEN_BITS = 1024;
	public static final String EMAIL_PATTERN =
		"^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
}
