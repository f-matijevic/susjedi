package hr.fer.susjedi.model.entity;

import hr.fer.susjedi.model.enums.VotingResult;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "conclusions")
@Data
public class Conclusion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "agenda_item_id", nullable = false)
    private AgendaItem agendaItem;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "voting_result")
    private VotingResult votingResult;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
