package ee.pw.security.securemarkdown.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptionTools {

	@SneakyThrows
	public static String encodeStringWithKeyWithAES(
		String content,
		String secretKey
	) {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
			Cipher.ENCRYPT_MODE,
			new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES")
		);
		byte[] encryptedData = cipher.doFinal(
			content.getBytes(StandardCharsets.UTF_8)
		);

		return Base64.getEncoder().encodeToString(encryptedData);
	}

	@SneakyThrows
	public static String decodeStringFromAESEncryption(
		String content,
		String secretKey
	) {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(
			Cipher.DECRYPT_MODE,
			new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES")
		);
		byte[] encryptedData = Base64.getDecoder().decode(content);
		byte[] decryptedContent = cipher.doFinal(encryptedData);

		return new String(decryptedContent, StandardCharsets.UTF_8);
	}
}
