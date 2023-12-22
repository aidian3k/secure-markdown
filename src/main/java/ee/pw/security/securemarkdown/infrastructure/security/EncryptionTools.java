package ee.pw.security.securemarkdown.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptionTools {

	@SneakyThrows
	public static String encodeStringWithKeyWithAES(
		@NonNull String content,
		@NonNull String password
	) {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec secretKeySpec = generateAESKey(password);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

		byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

		return Base64.getEncoder().encodeToString(encrypted);
	}

	@SneakyThrows
	public static String decodeStringFromAESEncryption(
		@NonNull String content,
		@NonNull String password
	) {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, generateAESKey(password));

		byte[] encryptedData = Base64.getDecoder().decode(content);
		byte[] decryptedContent = cipher.doFinal(encryptedData);

		return new String(decryptedContent, StandardCharsets.UTF_8);
	}

	@SneakyThrows
	private static SecretKeySpec generateAESKey(String password) {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] keyBytes = sha.digest(password.getBytes(StandardCharsets.UTF_8));
		keyBytes = Arrays.copyOf(keyBytes, 16);

		return new SecretKeySpec(keyBytes, "AES");
	}
}
