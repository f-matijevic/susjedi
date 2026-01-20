package hr.fer.susjedi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StanBlogDiscussionDTO {

    @JsonProperty("naslov")
    private String naslov;

    @JsonProperty("poveznica")
    private String poveznica;

    @JsonProperty("pitanje")
    private String pitanje;

    public StanBlogDiscussionDTO() {}

    public StanBlogDiscussionDTO(String naslov, String poveznica, String pitanje) {
        this.naslov = naslov;
        this.poveznica = poveznica;
        this.pitanje = pitanje;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getPoveznica() {
        return poveznica;
    }

    public void setPoveznica(String poveznica) {
        this.poveznica = poveznica;
    }

    public String getPitanje() {
        return pitanje;
    }

    public void setPitanje(String pitanje) {
        this.pitanje = pitanje;
    }

    @Override
    public String toString() {
        return "StanBlogDiscussionDTO{" +
                "naslov='" + naslov + '\'' +
                ", poveznica='" + poveznica + '\'' +
                ", pitanje='" + pitanje + '\'' +
                '}';
    }
}