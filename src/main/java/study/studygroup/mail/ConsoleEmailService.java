package study.studygroup.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

@Profile("local")
@Component
@Slf4j
public class ConsoleEmailService implements EmailService {
    @Override
    public void sendEmail(EmailMessage emailMessage) {
      log.info("sent email: {}", emailMessage.getMessage());
    }
}
