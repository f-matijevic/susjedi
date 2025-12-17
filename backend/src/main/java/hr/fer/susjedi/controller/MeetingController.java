package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.request.CreateAgendaItemRequest;
import hr.fer.susjedi.model.request.CreateMeetingRequest;
import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

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

}