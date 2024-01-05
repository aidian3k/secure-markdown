package ee.pw.security.securemarkdown.infrastructure.security;

import ee.pw.security.securemarkdown.domain.note.entity.Note;
import io.vavr.Tuple3;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EncryptionTools {

	private static final int ITERATIONS = 256;
	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

	@SneakyThrows
	public static Tuple3<String, byte[], byte[]> encodeStringWithKeyWithAES(
		@NonNull String content,
		@NonNull String password
	) {
		byte[] salt = generateRandomBytes();
		SecretKey secretKey = generateAESKey(password, salt);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(
			generateRandomBytes()
		);
		String encryptedNoteText = encrypt(content, secretKey, ivParameterSpec);

		return new Tuple3<>(encryptedNoteText, salt, ivParameterSpec.getIV());
	}

	@SneakyThrows
	public static String decodeStringFromAESEncryption(
		@NonNull Note note,
		@NonNull String password
	) {
		byte[] salt = note.getSalt();
		SecretKey key = generateAESKey(password, salt);
		IvParameterSpec iv = new IvParameterSpec(note.getIv());

        return decrypt(note.getContent(), key, iv);
	}

	@SneakyThrows
	private static SecretKeySpec generateAESKey(String password, byte[] salt) {
		SecretKeyFactory factory = SecretKeyFactory.getInstance(
			"PBKDF2WithHmacSHA256"
		);
		KeySpec spec = new PBEKeySpec(
			password.toCharArray(),
			salt,
			ITERATIONS,
			256
		);

		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}

	@SneakyThrows
	public static String encrypt(
		String input,
		SecretKey key,
		IvParameterSpec iv
	) {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		byte[] cipherText = cipher.doFinal(input.getBytes());
		return Base64.getEncoder().encodeToString(cipherText);
	}

	private static byte[] generateRandomBytes() {
		SecureRandom random = new SecureRandom();
		byte[] res = new byte[16];
		random.nextBytes(res);
		return res;
	}

	@SneakyThrows
	public static String decrypt(
		String cipherText,
		SecretKey key,
		IvParameterSpec iv
	) {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);

		byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(plainText);
	}
}
