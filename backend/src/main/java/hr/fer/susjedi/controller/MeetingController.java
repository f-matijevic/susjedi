package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.request.CreateAgendaItemRequest;
import hr.fer.susjedi.model.request.CreateConclusionRequest;
import hr.fer.susjedi.model.request.CreateMeetingRequest;
import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.service.MeetingService;
import hr.fer.susjedi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getAllMeetings() {
        log.info("GET /api/meetings - Fetching all meetings");
        List<MeetingDTO> meetings = meetingService.getAllMeetings();
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MeetingDTO>> getMyMeetings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("GET /api/meetings/my - Fetching meetings for user {}", user.getEmail());
        List<MeetingDTO> meetings = meetingService.getMeetingsByUser(user);
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingDTO> getMeetingById(@PathVariable Long id) {
        log.info("GET /api/meetings/{} - Fetching meeting by ID", id);
        MeetingDTO meeting = meetingService.getMeetingById(id);
        return ResponseEntity.ok(meeting);
    }

    @PostMapping("/{id}/agenda-items")
    @PreAuthorize("hasRole('PREDSTAVNIK')")
    public ResponseEntity<?> addAgendaItem(
            @PathVariable Long id,
            @Valid @RequestBody CreateAgendaItemRequest request) {

        log.info("POST /api/meetings/{}/agenda-items - Adding agenda item", id);
        meetingService.addAgendaItem(id, request);
        return ResponseEntity.ok("Točka dnevnog reda uspješno dodana.");
    }

    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        log.info("POST /api/meetings - Creating new meeting: {}", request.getTitle());
        MeetingDTO created = meetingService.createMeeting(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('PREDSTAVNIK')")
    public ResponseEntity<?> publishMeeting(@PathVariable Long id) {
        log.info("PUT /api/meetings/{}/publish - Publishing meeting", id);
        meetingService.publishMeeting(id);
        return ResponseEntity.ok("Sastanak je uspješno objavljen.");
    }

    @GetMapping("/published")
    public ResponseEntity<List<MeetingDTO>> getPublishedMeetings() {
        log.info("GET /api/meetings/published");
        List<MeetingDTO> meetings = meetingService.getPublishedMeetings();
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('SUVLASNIK')")
    public ResponseEntity<?> confirmAttendance(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        log.info("Korisnik {} potvrđuje dolazak na sastanak {}", user.getUsername(), id);

        meetingService.confirmAttendance(id, user);

        return ResponseEntity.ok("Dolazak potvrđen.");
    }

    @PostMapping("/agenda-items/{itemId}/conclusion")
    @PreAuthorize("hasRole('PREDSTAVNIK')")
    public ResponseEntity<?> addConclusion(
            @PathVariable Long itemId,
            @RequestBody CreateConclusionRequest request) {

        log.info("Dodavanje zaključka za točku ID: {}", itemId);

        meetingService.addConclusion(
                itemId,
                request.getContent(),
                request.getVotingResult()
        );

        return ResponseEntity.ok("Zaključak uspješno dodan.");
    }
    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('PREDSTAVNIK')")
    public ResponseEntity<?> completeMeeting(@PathVariable Long id) {
        meetingService.completeMeeting(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveMeeting(@PathVariable Long id) {
        try {
            meetingService.archiveMeeting(id);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}