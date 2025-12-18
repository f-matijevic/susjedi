package hr.fer.susjedi.model.response;

import lombok.Data;

@Data
public class AgendaItemDTO {
    private Long id;
    private String title;
    private String description;
    private Integer orderNumber;
    private Boolean hasLegalEffect;
    private Boolean requiresVoting;
    private String stanblogDiscussionUrl;
    private ConclusionDTO conclusion;
}