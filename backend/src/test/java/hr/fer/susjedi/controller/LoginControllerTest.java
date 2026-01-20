package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.repository.UserRepository;
import hr.fer.susjedi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; 
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false) 
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    public void testUspjesnaPrijava_K1() throws Exception {
        String email = "marko@stanblog.com";
        String lozinka = "Marko123$";

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setUsername("marko");
        mockUser.setPassword(encoder.encode(lozinka));

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        Mockito.when(jwtService.generateToken(any())).thenReturn("token123");

        String loginJson = "{\"email\":\"" + email + "\", \"lozinka\":\"" + lozinka + "\"}";

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prijava uspješna!"));
    }

    //Prijava s neispravnom lozinkom 
    @Test
    public void testNeispravnaLozinka_K2() throws Exception {
        String email = "marko@stanblog.com";
        String ispravnaLozinkaEnkodirana = encoder.encode("Marko123$");
        String pogresnaLozinkaUnos = "Mislav92%";

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword(ispravnaLozinkaEnkodirana);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        String loginJson = "{\"email\":\"" + email + "\", \"lozinka\":\"" + pogresnaLozinkaUnos + "\"}";

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pogrešna lozinka!"));
    }

    //Prijava s nepostojećim korisnikom 
    @Test
    public void testNepostojeciKorisnik_K3() throws Exception {
        String email = "trpimir@stanblog.com";
        String lozinka = "Mislav92%";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        String loginJson = "{\"email\":\"" + email + "\", \"lozinka\":\"" + lozinka + "\"}";

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Korisnik ne postoji!"));
    }

    // Prijava s praznim poljima 
    @Test
    public void testPraznaPolja_K4() throws Exception {

        String loginJson = "{\"email\":\"\", \"lozinka\":\"\"}";

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Korisnik ne postoji!"));
    }
}