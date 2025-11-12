package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.request.LoginRequest;
import hr.fer.susjedi.repository.UserRepository;
import hr.fer.susjedi.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.email);
        if (user.isEmpty()) {
            return Map.of("message", "Korisnik ne postoji!");
        }

        if (!passwordEncoder.matches(request.lozinka, user.get().getPassword())) {
            return Map.of("message", "Pogrešna lozinka!");
        }

        String token = jwtService.generateToken(user.orElse(null));

        return Map.of(
                "message", "Prijava uspješna!",
                "username", user.get().getUsername(),
                "token", token
        );
    }
}