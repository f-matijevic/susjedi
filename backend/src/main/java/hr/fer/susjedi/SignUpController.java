package hr.fer.susjedi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public String register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.email)) {
            return "Email je već registriran!";
        }

        UserEntity k = new UserEntity();
        k.username = request.Name;
        k.email = request.email;
        k.password = passwordEncoder.encode(request.lozinka);
        k.role = "SUVLASNIK";

        userRepository.save(k);
        System.out.println("Uspjeh");
        System.out.println(k.password);
        return "Korisnik " + request.Name + " uspješno registriran!";
    }
}