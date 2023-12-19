package ee.pw.security.securemarkdown.infrastructure.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityTools {

	public static double calculateEntropy(String password) {
		int possibleCharacters = countPossibleCharacters(password);

		return Math.log(possibleCharacters) / Math.log(2);
	}

	private static int countPossibleCharacters(String password) {
		int minAscii = 33;
		int maxAscii = 126;

		int possibleCharacters = 0;

		for (char character : password.toCharArray()) {
			if (character >= minAscii && character <= maxAscii) {
				possibleCharacters++;
			}
		}

		return possibleCharacters;
	}
}
