package ee.pw.security.securemarkdown.infrastructure.mail;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender javaMailSender;

	@Async
	public void sendEmail(String toEmail, String subject, String content)
		throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		mimeMessage.setText(content);
		mimeMessage.setSubject(subject);
		mimeMessage.setRecipient(
			Message.RecipientType.TO,
			new InternetAddress(toEmail)
		);

		javaMailSender.send(mimeMessage);
	}
}
