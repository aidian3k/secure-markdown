package ee.pw.security.securemarkdown.infrastructure.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import ee.pw.security.securemarkdown.infrastructure.exception.GenericAppException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	public static String generateQRCode(String data) {
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		try {
			Writer writer = new QRCodeWriter();
			BitMatrix bitMatrix = writer.encode(
				data,
				BarcodeFormat.QR_CODE,
				300,
				300,
				hints
			);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

			byte[] imageBytes = outputStream.toByteArray();
			return (
				"data:image/png;base64," +
				java.util.Base64.getEncoder().encodeToString(imageBytes)
			);
		} catch (IOException | WriterException exception) {
			throw new GenericAppException(
				"Exception occurred when trying to generate QR code"
			);
		}
	}
}
