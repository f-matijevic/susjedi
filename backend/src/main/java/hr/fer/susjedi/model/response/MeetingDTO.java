package hr.fer.susjedi.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class MeetingDTO {
    private Long id;
    private String title;
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime meetingDatetime;


    private String location;
    private String state;
    private String createdByEmail;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private OffsetDateTime createdAt;

    private Integer agendaItemsCount;
    private List<AgendaItemDTO> agendaItems;
}