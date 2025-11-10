package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.request.RegisterRequest;
import hr.fer.susjedi.repository.UserRepository;
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

        User k = new User();
        k.setUsername(request.Name);
        k.setEmail(request.email);
        k.setPassword(passwordEncoder.encode(request.lozinka));
        k.setRole(String.valueOf(request.role));
        userRepository.save(k);
        response.put("success", "Korisnik " + request.Name + " uspješno registriran!");
        return response;
    }
}