package hr.fer.susjedi.service;

import hr.fer.susjedi.model.entity.Meeting;
import hr.fer.susjedi.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate;

    @Async
    public void sendMeetingPublishedNotification(Meeting meeting, List<User> coowners) {
        if (!emailEnabled) {
            log.info("Email slanje je onemogućeno");
            return;
        }

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

        int successCount = 0;
        int failCount = 0;

        for (User user : coowners) {
            if (sendEmail(user.getEmail(), subject, body)) {
                successCount++;
                log.debug("Email poslan korisniku: {}", user.getEmail());
            } else {
                failCount++;
            }
        }

        log.info("Završeno slanje obavijesti: {} uspješno, {} neuspješno",
                successCount, failCount);
    }

    @Async
    public void sendMeetingArchivedNotification(Meeting meeting, List<User> coowners) {
        if (!emailEnabled) {
            log.info("Email slanje je onemogućeno");
            return;
        }

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

        int successCount = 0;
        int failCount = 0;

        for (User user : coowners) {
            if (sendEmail(user.getEmail(), subject, body)) {
                successCount++;
                log.debug("Email poslan korisniku: {}", user.getEmail());
            } else {
                failCount++;
            }
        }

        log.info("Završeno slanje obavijesti: {} uspješno, {} neuspješno",
                successCount, failCount);
    }

    private boolean sendEmail(String to, String subject, String body) {
        try {
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            Map<String, Object> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", "StanPlan");

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", to);

            Map<String, Object> emailData = new HashMap<>();
            emailData.put("sender", sender);
            emailData.put("to", List.of(recipient));
            emailData.put("subject", subject);
            emailData.put("textContent", body);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(emailData, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Email uspješno poslan na: {}", to);
                return true;
            } else {
                log.error("Brevo API vratio neočekivani status: {}", response.getStatusCode());
                return false;
            }

        } catch (HttpClientErrorException e) {
            log.error("HTTP Error {} pri slanju emaila na {}: {}",
                    e.getStatusCode(), to, e.getResponseBodyAsString());
            return false;

        } catch (Exception e) {
            log.error("Greška pri slanju emaila na {}: {}", to, e.getMessage());
            return false;
        }
    }
}