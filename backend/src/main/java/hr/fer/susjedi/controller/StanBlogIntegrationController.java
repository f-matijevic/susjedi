package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.request.CreateMeetingFromDiscussionRequest;
import hr.fer.susjedi.model.response.CreateMeetingFromDiscussionResponse;
import hr.fer.susjedi.model.response.StanBlogDiscussionDTO;
import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.service.MeetingService;
import hr.fer.susjedi.service.StanBlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class StanBlogIntegrationController {

    private final StanBlogService stanBlogService;
    private final MeetingService meetingService;

    @GetMapping("/stanblog/discussions")
    public ResponseEntity<List<StanBlogDiscussionDTO>> getDiscussions() {
        try {
            List<StanBlogDiscussionDTO> discussions =
                    stanBlogService.getDiscussionsWithPositiveVoting();
            log.info("Vracam {} diskusija", discussions.size());
            return ResponseEntity.ok(discussions);
        } catch (Exception e) {
            log.error("Greška: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sastanci-iz-diskusije/")
    public ResponseEntity<?> createMeetingFromDiscussion(
            @RequestBody CreateMeetingFromDiscussionRequest request) {
        try {
            if (request.getNaslov() == null || request.getNaslov().trim().isEmpty()) {
                log.warn("Nedostaje 'naslov'");
                return ResponseEntity.badRequest()
                        .body("Polje 'naslov' je obavezno");
            }
            if (request.getTermin() == null || request.getTermin().trim().isEmpty()) {
                log.warn("Nedostaje 'termin'");
                return ResponseEntity.badRequest()
                        .body("Polje 'termin' je obavezno");
            }
            if (request.getTockaDnevnogReda() == null || request.getTockaDnevnogReda().trim().isEmpty()) {
                log.warn("Nedostaje 'tocka_dnevnog_reda'");
                return ResponseEntity.badRequest()
                        .body("Polje 'tocka_dnevnog_reda' je obavezno");
            }

            MeetingDTO meetingDTO = meetingService.createMeetingFromStanBlogDiscussion(
                    request.getNaslov(),
                    request.getTermin(),
                    request.getTockaDnevnogReda(),
                    request.getCiljSastanka()
            );
            CreateMeetingFromDiscussionResponse response = new CreateMeetingFromDiscussionResponse();
            response.setIdSastanak(meetingDTO.getId());
            response.setNaslov(meetingDTO.getTitle());

            String formattedDate = meetingDTO.getMeetingDatetime()
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            response.setDatumVrijeme(formattedDate);
            response.setStatus(meetingDTO.getState());
            response.setIzDiskusije(true);

            log.info("Sastanak uspješno kreiran: ID={}", response.getIdSastanak());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Krivi format requesta: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            log.error("Greška u bazi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Interna greška servera: " + e.getMessage());
        }
    }
}
