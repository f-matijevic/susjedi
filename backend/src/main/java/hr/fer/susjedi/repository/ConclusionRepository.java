package hr.fer.susjedi.repository;

import hr.fer.susjedi.model.entity.Conclusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConclusionRepository extends JpaRepository<Conclusion, Long> {
    Optional<Conclusion> findByAgendaItemId(Long agendaItemId);
}