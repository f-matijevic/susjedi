package hr.fer.susjedi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agenda_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "has_legal_effect", nullable = false)
    private Boolean hasLegalEffect = false;

    @Column(name = "requires_voting", nullable = false)
    private Boolean requiresVoting = false;

    @Column(name = "stanblog_discussion_url", length = 500)
    private String stanBlogDiscussionUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "voting_question", columnDefinition = "TEXT")
    private String votingQuestion;

    @OneToOne(mappedBy = "agendaItem", cascade = CascadeType.ALL)
    private Conclusion conclusion;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}