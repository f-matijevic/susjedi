package hr.fer.susjedi.service;

import hr.fer.susjedi.model.entity.Meeting;
import hr.fer.susjedi.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Async
    public void sendMeetingPublishedNotification(Meeting meeting, List<User> coowners) {

        log.info("Slanje obavijesti o objavi sastanka '{}' na {} adresa",
                meeting.getTitle(), coowners.size());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String meetingDateTime = meeting.getMeetingDatetime().format(formatter);

        String subject = "StanPlan - Novi sastanak objavljen: " + meeting.getTitle();

        String body = String.format(
                "Poštovani,\n\n" +
                        "Objavljen je novi sastanak suvlasnika:\n\n" +
                        "Naslov: %s\n" +
                        "Datum i vrijeme: %s\n" +
                        "Lokacija: %s\n\n" +
                        "Sažetak:\n%s\n\n" +
                        "Molimo potvrdite svoj dolazak putem StanPlan aplikacije.\n\n" +
                        "Lijep pozdrav,\n" +
                        "StanPlan Tim",
                meeting.getTitle(),
                meetingDateTime,
                meeting.getLocation(),
                meeting.getSummary()
        );

        for (User user : coowners) {
            sendSimpleEmail(user.getEmail(), subject, body);
            log.debug("Email poslan korisniku: {}", user.getEmail());
        }

        log.info("Završeno slanje obavijesti o objavi sastanka");
    }

    @Async
    public void sendMeetingArchivedNotification(Meeting meeting, List<User> coowners) {
        log.info("Slanje obavijesti o arhiviranju sastanka '{}' na {} adresa",
                meeting.getTitle(), coowners.size());

        String subject = "StanPlan - Sastanak arhiviran: " + meeting.getTitle();

        String body = String.format(
                "Poštovani,\n\n" +
                        "Sastanak '%s' je arhiviran.\n\n" +
                        "Možete pregledati zaključke sastanka u StanPlan aplikaciji.\n\n" +
                        "Lijep pozdrav,\n" +
                        "StanPlan Tim",
                meeting.getTitle()
        );

        for (User user : coowners) {
            sendSimpleEmail(user.getEmail(), subject, body);
            log.debug("Email poslan korisniku: {}", user.getEmail());
        }
        log.info("Završeno slanje obavijesti o arhiviranju sastanka");
    }

    private void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.debug("Email uspješno poslan na: {}", to);
    }
}