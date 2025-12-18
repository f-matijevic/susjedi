package hr.fer.susjedi.model.request;
import lombok.Data;

@Data
public class CreateConclusionRequest {
    private String content;
    private String votingResult;
}