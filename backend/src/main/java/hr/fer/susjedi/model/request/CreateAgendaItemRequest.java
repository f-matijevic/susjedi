package hr.fer.susjedi.model.request;

import lombok.Data;

@Data
public class CreateAgendaItemRequest {
    private String title;
    private String description;
    private Integer orderNumber;
    private Boolean hasLegalEffect;
    private Boolean requiresVoting;
    private String stanblogDiscussionUrl;
}