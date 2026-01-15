package hr.fer.susjedi.controller;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.model.request.RegisterRequest;
import hr.fer.susjedi.repository.UserRepository;
import hr.fer.susjedi.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SignUpController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SignUpController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        Map<String, String> response = new HashMap<>();

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("message", "Niste prijavljeni (nedostaje token)!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                response.put("message", "Korisnik nije prijavljen.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String role = jwtService.extractRole(token);
            if (!"ADMIN".equals(role)) {
                response.put("message", "Samo administrator može kreirati nove korisnike!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Greška prilikom provjere tokena.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (userRepository.existsByEmail(request.email)) {
            response.put("message", "Korisnik s ovim emailom već postoji!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User k = new User();
        k.setUsername(request.Name);
        k.setEmail(request.email);
        k.setPassword(passwordEncoder.encode(request.lozinka));
        k.setRole(String.valueOf(request.role));
        userRepository.save(k);

        response.put("message", "Korisnik " + request.Name + " uspješno registriran!");
        return ResponseEntity.ok(response);
    }
}