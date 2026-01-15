package hr.fer.susjedi.repository;

import hr.fer.susjedi.model.entity.MeetingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingAttendanceRepository extends JpaRepository<MeetingAttendance, Long> {
    boolean existsByMeetingIdAndUserId(Long meetingId, Long userId);
    List<MeetingAttendance> findByMeetingId(Long meetingId);
}