package hr.fer.susjedi.service;

import hr.fer.susjedi.model.entity.AgendaItem;
import hr.fer.susjedi.model.enums.MeetingState;
import hr.fer.susjedi.model.request.CreateAgendaItemRequest;
import hr.fer.susjedi.model.request.CreateMeetingRequest;
import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.model.entity.Meeting;
import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.response.AgendaItemDTO;
import hr.fer.susjedi.repository.AgendaItemRepository;
import hr.fer.susjedi.repository.MeetingRepository;
import hr.fer.susjedi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {

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
        log.info("Fetching all meetings");
        return meetingRepository.findAllByOrderByMeetingDatetimeDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> getMeetingsByUser(User user) {
        List<Meeting> meetings = meetingRepository.findByCreatedBy(user);
        return meetings.stream()
                .map(this::toDTO)
                .toList();
    }
    public MeetingDTO getMeetingById(Long id) {
        log.info("Fetching meeting by ID: {}", id);
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sastanak nije pronađen"));
        return toDTO(meeting);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Korisnik nije pronađen"));
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
            }).toList());
        }

        return dto;
    }
    public void addAgendaItem(Long meetingId, CreateAgendaItemRequest request) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Sastanak nije pronađen"));

        if (meeting.getState() != MeetingState.PLANIRAN) {
            throw new RuntimeException("Točke dnevnog reda se mogu dodavati samo planiranim sastancima.");
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

}