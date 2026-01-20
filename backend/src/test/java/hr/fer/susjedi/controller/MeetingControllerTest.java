package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.response.MeetingDTO;
import hr.fer.susjedi.service.MeetingService;
import hr.fer.susjedi.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private UserService userService;

    // Kreiranje sastanka
    @Test
    public void testKreiranjeSastanka_K5() throws Exception {
        String meetingJson = "{\"title\":\"Sastanak stanara\", \"description\":\"Dogovor o krovu\", \"dateTime\":\"2026-06-01T18:00:00\"}";

        MeetingDTO mockResponse = new MeetingDTO();
        mockResponse.setTitle("Sastanak stanara");
        Mockito.when(meetingService.createMeeting(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(meetingJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sastanak stanara"));
    }

    // Sastanak bez naslova
    @Test
    public void testSastanakBezNaslova_K6() throws Exception {
        String badJson = "{\"title\":\"\", \"description\":\"Test\"}";

        Mockito.when(meetingService.createMeeting(any()))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "Naslov je obavezan"));

        mockMvc.perform(post("/api/meetings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andDo(print()) 
                .andExpect(status().isBadRequest());
    }
}