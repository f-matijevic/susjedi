package hr.fer.susjedi.service;

import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.repository.UserRepository;
import hr.fer.susjedi.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2Service {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public String handleOAuth2Login(OAuth2User oauth2User, String provider) {
        String email = oauth2User.getAttribute("email");
        String providerId = oauth2User.getAttribute("sub");
        String name = oauth2User.getAttribute("name");

        log.info("OAuth2 login attempt - Provider: {}, Email: {}", provider, email);

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
        if (existingUser.isEmpty()) {
            log.info("Creating new OAuth2 user: {}", email);

            user = new User();
            user.setEmail(email);
            user.setOauthProvider(provider.toUpperCase());
            user.setOauthProviderId(providerId);
            user.setUsername(email);
            user.setPassword(null);


            user.setRole("SUVLASNIK");

            user = userRepository.save(user);

            log.info("New OAuth2 user created with ID: {}", user.getId());
        } else {
            user = existingUser.get();
            log.info("Existing user found: {}", user.getId());


            if (user.getOauthProvider() == null) {
                user.setOauthProvider(provider.toUpperCase());
                user.setOauthProviderId(providerId);
                userRepository.save(user);
            }
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user);

        log.info("JWT token generated for user: {}", user.getEmail());

        return token;
    }
}