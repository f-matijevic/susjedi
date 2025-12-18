package hr.fer.susjedi.service;

import hr.fer.susjedi.model.entity.AgendaItem;
import hr.fer.susjedi.model.entity.MeetingAttendance;
import hr.fer.susjedi.model.enums.MeetingState;
import hr.fer.susjedi.model.request.CreateAgendaItemRequest;
import hr.fer.susjedi.model.request.CreateMeetingRequest;
import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.model.entity.Meeting;
import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.response.AgendaItemDTO;
import hr.fer.susjedi.repository.AgendaItemRepository;
import hr.fer.susjedi.repository.MeetingAttendanceRepository;
import hr.fer.susjedi.repository.MeetingRepository;
import hr.fer.susjedi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

    private final MeetingAttendanceRepository attendanceRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final AgendaItemRepository agendaItemRepository;

    @Transactional
    public MeetingDTO createMeeting(CreateMeetingRequest request) {
        User currentUser = getCurrentUser();
        if (!"PREDSTAVNIK".equals(currentUser.getRole())) {
            throw new IllegalStateException("Samo predstavnik može kreirati sastanke");
        }

        Meeting meeting = new Meeting();
        meeting.setTitle(request.getTitle());
        meeting.setSummary(request.getSummary());
        meeting.setMeetingDatetime(request.getMeetingDatetime());
        meeting.setLocation(request.getLocation());
        meeting.setState(MeetingState.PLANIRAN);
        meeting.setCreatedBy(currentUser);
        meeting = meetingRepository.save(meeting);
        return toDTO(meeting);
    }

    public List<MeetingDTO> getAllMeetings() {
        return meetingRepository.findAllByOrderByMeetingDatetimeDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> getMeetingsByUser(User user) {
        return meetingRepository.findByCreatedBy(user).stream()
                .map(this::toDTO)
                .toList();
    }

    public MeetingDTO getMeetingById(Long id) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sastanak nije pronađen"));
        return toDTO(meeting);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Niste prijavljeni.");
        }

        String email;
        Object principal = authentication.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else if (principal instanceof User userEntity) {
            email = userEntity.getEmail();
        } else {
            email = authentication.getName();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Korisnik s emailom " + email + " nije pronađen"));
    }

    private MeetingDTO toDTO(Meeting meeting) {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setTitle(meeting.getTitle());
        dto.setSummary(meeting.getSummary());
        dto.setMeetingDatetime(meeting.getMeetingDatetime());
        dto.setLocation(meeting.getLocation());
        dto.setState(meeting.getState().name());
        dto.setCreatedByEmail(meeting.getCreatedBy().getEmail());
        dto.setCreatedAt(meeting.getCreatedAt());

        List<MeetingAttendance> attendances = attendanceRepository.findByMeetingId(meeting.getId());

        dto.setAttendeeUsernames(attendances.stream()
                .map(a -> a.getUser().getUsername())
                .collect(Collectors.toList()));

        dto.setCurrentUserAttending(false); // default

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                User currentUser = null;

                if (auth.getPrincipal() instanceof User userEntity) {
                    currentUser = userEntity;
                } else if (auth.getPrincipal() instanceof OAuth2User oAuth2) {
                    String email = oAuth2.getAttribute("email");
                    currentUser = userRepository.findByEmail(email).orElse(null);
                } else {
                    String email = auth.getName();
                    currentUser = userRepository.findByEmail(email).orElse(null);
                }

                if (currentUser != null) {
                    final Long userId = currentUser.getId();
                    boolean confirmed = attendances.stream()
                            .anyMatch(a -> a.getUser().getId().equals(userId));

                    log.info("Sastanak {}: Korisnik ID={} (email={}) potvrđen -> {}",
                            meeting.getId(), userId, currentUser.getEmail(), confirmed);
                    dto.setCurrentUserAttending(confirmed);
                }
            }
        } catch (Exception e) {
            log.error("Greška pri provjeri potvrde u toDTO: ", e);
            dto.setCurrentUserAttending(false);
        }

        dto.setAgendaItemsCount(meeting.getAgendaItems() != null ? meeting.getAgendaItems().size() : 0);
        if (meeting.getAgendaItems() != null) {
            dto.setAgendaItems(meeting.getAgendaItems().stream().map(item -> {
                AgendaItemDTO itemDto = new AgendaItemDTO();
                itemDto.setId(item.getId());
                itemDto.setTitle(item.getTitle());
                itemDto.setDescription(item.getDescription());
                itemDto.setOrderNumber(item.getOrderNumber());
                itemDto.setHasLegalEffect(item.getHasLegalEffect());
                itemDto.setRequiresVoting(item.getRequiresVoting());
                itemDto.setStanblogDiscussionUrl(item.getStanblogDiscussionUrl());
                return itemDto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    public void addAgendaItem(Long meetingId, CreateAgendaItemRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Sastanak nije pronađen"));
        if (meeting.getState() != MeetingState.PLANIRAN) {
            throw new RuntimeException("Točke se dodaju samo planiranim sastancima.");
        }
        AgendaItem item = new AgendaItem();
        item.setMeeting(meeting);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setOrderNumber(request.getOrderNumber());
        item.setHasLegalEffect(request.getHasLegalEffect());
        item.setRequiresVoting(request.getRequiresVoting());
        item.setStanblogDiscussionUrl(request.getStanblogDiscussionUrl());
        agendaItemRepository.save(item);
    }

    @Transactional
    public void publishMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Sastanak nije pronađen"));
        if (meeting.getState() != MeetingState.PLANIRAN) {
            throw new RuntimeException("Samo planirani sastanci se mogu objaviti.");
        }
        meeting.setState(MeetingState.OBJAVLJEN);
        meetingRepository.save(meeting);
    }

    public List<MeetingDTO> getPublishedMeetings() {
        return meetingRepository.findByState(MeetingState.OBJAVLJEN).stream()
                .map(this::toDTO)
                .toList();
    }

    public void confirmAttendance(Long meetingId, User user) {
        User dbUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen."));

        if (!"SUVLASNIK".equals(dbUser.getRole())) {
            throw new RuntimeException("Samo suvlasnici mogu potvrditi dolazak.");
        }
        if (attendanceRepository.existsByMeetingIdAndUserId(meetingId, dbUser.getId())) {
            throw new RuntimeException("Već ste potvrdili dolazak.");
        }
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Sastanak nije pronađen"));
        if (meeting.getState() != MeetingState.OBJAVLJEN) {
            throw new RuntimeException("Sastanak nije objavljen.");
        }

        MeetingAttendance attendance = new MeetingAttendance();
        attendance.setMeeting(meeting);
        attendance.setUser(dbUser);
        attendance.setConfirmedAt(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }
}