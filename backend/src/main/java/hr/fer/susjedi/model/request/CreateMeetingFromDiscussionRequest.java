package hr.fer.susjedi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateMeetingFromDiscussionRequest {

    @JsonProperty("naslov")
    private String naslov;

    @JsonProperty("termin")
    private String termin;  // ISO 8601 format: "2026-02-10T16:00:00Z"

    @JsonProperty("tocka_dnevnog_reda")
    private String tockaDnevnogReda;

    @JsonProperty("cilj_sastanka")
    private String ciljSastanka;

    public CreateMeetingFromDiscussionRequest() {}

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getTermin() {
        return termin;
    }

    public void setTermin(String termin) {
        this.termin = termin;
    }

    public String getTockaDnevnogReda() {
        return tockaDnevnogReda;
    }

    public void setTockaDnevnogReda(String tockaDnevnogReda) {
        this.tockaDnevnogReda = tockaDnevnogReda;
    }

    public String getCiljSastanka() {
        return ciljSastanka;
    }

    public void setCiljSastanka(String ciljSastanka) {
        this.ciljSastanka = ciljSastanka;
    }

    @Override
    public String toString() {
        return "CreateMeetingFromDiscussionRequest{" +
                "naslov='" + naslov + '\'' +
                ", termin='" + termin + '\'' +
                ", tockaDnevnogReda='" + tockaDnevnogReda + '\'' +
                ", ciljSastanka='" + ciljSastanka + '\'' +
                '}';
    }
}