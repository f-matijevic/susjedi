package hr.fer.susjedi.service;

import hr.fer.susjedi.model.request.UpdateUserRoleRequest;
import hr.fer.susjedi.model.response.UserDTO;
import hr.fer.susjedi.model.entity.User;
import hr.fer.susjedi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO updateUserRole(Long userId, UpdateUserRoleRequest request) {
        log.info("Updating role for user ID: {} to {}", userId, request.getRole());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Korisnik nije pronađen"));

        user.setRole(request.getRole());
        user = userRepository.save(user);

        log.info("User role updated successfully");
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setOauthProvider(user.getOauthProvider());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        return dto;
    }
}