package hr.fer.susjedi.model.response;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateMeetingFromDiscussionResponse {

    @JsonProperty("id_sastanak")
    private Long idSastanak;

    @JsonProperty("naslov")
    private String naslov;

    @JsonProperty("datum_vrijeme")
    private String datumVrijeme;

    @JsonProperty("status")
    private String status;

    @JsonProperty("iz_diskusije")
    private Boolean izDiskusije;

    public CreateMeetingFromDiscussionResponse() {}

    public CreateMeetingFromDiscussionResponse(Long idSastanak, String naslov,
                                               String datumVrijeme, String status,
                                               Boolean izDiskusije) {
        this.idSastanak = idSastanak;
        this.naslov = naslov;
        this.datumVrijeme = datumVrijeme;
        this.status = status;
        this.izDiskusije = izDiskusije;
    }

    public Long getIdSastanak() {
        return idSastanak;
    }

    public void setIdSastanak(Long idSastanak) {
        this.idSastanak = idSastanak;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getDatumVrijeme() {
        return datumVrijeme;
    }

    public void setDatumVrijeme(String datumVrijeme) {
        this.datumVrijeme = datumVrijeme;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIzDiskusije() {
        return izDiskusije;
    }

    public void setIzDiskusije(Boolean izDiskusije) {
        this.izDiskusije = izDiskusije;
    }
}