package hr.fer.susjedi.repository;

import hr.fer.susjedi.model.entity.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaItemRepository extends JpaRepository<AgendaItem, Long> {
    List<AgendaItem> findByMeetingIdOrderByOrderNumberAsc(Long meetingId);
}