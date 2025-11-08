package hr.fer.susjedi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SignUpController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SignUpController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody RegisterRequest request) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByEmail(request.email)) {
            response.put("error", "Email je već registriran!");
            return response;
        }

        UserEntity k = new UserEntity();
        k.username = request.Name;
        k.email = request.email;
        k.password = passwordEncoder.encode(request.lozinka);
        k.role = "SUVLASNIK";

        userRepository.save(k);
        response.put("success", "Korisnik " + request.Name + " uspješno registriran!");
        return response;
    }
}