package hr.fer.susjedi.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateMeetingRequest {

    @NotBlank(message = "Naslov je obavezan")
    @Size(max = 150, message = "Naslov može imati max 150 znakova")
    private String title;

    @NotBlank(message = "Sažetak je obavezan")
    private String summary;

    @NotNull(message = "Datum i vrijeme su obavezni")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")  // ✅ PROMIJENI OVO - XXX umjesto X!
    private OffsetDateTime meetingDatetime;

    @NotBlank(message = "Lokacija je obavezna")
    @Size(max = 150, message = "Lokacija može imati max 150 znakova")
    private String location;
}