package hr.fer.susjedi.service;

import hr.fer.susjedi.model.response.StanBlogDiscussionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class StanBlogService {

    @Value("${stanblog.api.url}")
    private String stanBlogApiUrl;

    @Value("${stanblog.base.url}")
    private String stanBlogBaseUrl;

    private final RestTemplate restTemplate;

    public StanBlogService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<StanBlogDiscussionDTO> getDiscussionsWithPositiveVoting() {
        String url = stanBlogApiUrl + "/fetch-positive-outcome-discussions";

        log.info("Dohvaćam diskusije iz StanBlog API-ja");
        log.info("URL: {}", url);

        try {
            StanBlogDiscussionDTO[] response = restTemplate.getForObject(
                    url,
                    StanBlogDiscussionDTO[].class
            );

            if (response != null && response.length > 0) {
                List<StanBlogDiscussionDTO> discussions = Arrays.asList(response);

                for (int i = 0; i < discussions.size(); i++) {
                    StanBlogDiscussionDTO d = discussions.get(i);
                    log.info("Diskusija #{}: naslov='{}', poveznica='{}', pitanje='{}'",
                            i + 1, d.getNaslov(), d.getPoveznica(), d.getPitanje());
                }
                return discussions;
            }

            log.warn("StanBlog vratio prazan response");
            return Collections.emptyList();

        } catch (RestClientException e) {
            log.error("Greška pri komunikaciji sa StanBlog API-jem");
            log.error("Greška: {}", e.getMessage());
            throw new RuntimeException(
                    "Nije moguće dohvatiti diskusije iz StanBloga. "
            );
        }
    }
}