package hr.fer.susjedi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email);
        if (user == null) {
            return Map.of("message", "Korisnik ne postoji!");
        }

        if (!passwordEncoder.matches(request.lozinka, user.password)) {
            return Map.of("message", "Pogrešna lozinka!");
        }

        return Map.of("message", "Prijava uspješna!", "username", user.username);
    }
}