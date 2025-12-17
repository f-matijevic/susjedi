package hr.fer.susjedi.repository;

import hr.fer.susjedi.model.entity.Meeting;
import hr.fer.susjedi.model.enums.MeetingState;
import hr.fer.susjedi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByCreatedBy(User user);

    List<Meeting> findByState(MeetingState state);

    List<Meeting> findAllByOrderByMeetingDatetimeDesc();
}