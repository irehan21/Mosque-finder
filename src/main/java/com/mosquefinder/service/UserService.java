package com.mosquefinder.service;

import com.mosquefinder.dto.LocationDto;
import com.mosquefinder.dto.MosqueDto;
import com.mosquefinder.dto.UserDto;
import com.mosquefinder.exception.ResourceNotFoundException;
import com.mosquefinder.model.Mosque;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.MosqueRepository;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MosqueService mosqueService;

    public User createUser(String name, String email, String encodedPassword) {

        User user = User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .verified(false)
                .roles("USER")
                .myMosques("")
                .createdAt(LocalDateTime.now())
                .build();

        return saveUser(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void verifyUser(String email) {
        User user = findByEmail(email);
        user.setVerified(true);
        saveUser(user);
    }

    public void updateLocation(String email, LocationDto locationDto) {
        User user = findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        user.setLocation(locationDto.toEntity());
        saveUser(user);
    }

    public void addFavoriteMosque(String userId, String mosqueId) {
        User user = findById(userId);
        List<String> favorites = user.getFavoriteMosques();

        if (!favorites.contains(mosqueId)) {
            favorites.add(mosqueId);
            user.setFavoriteMosques(favorites);
            saveUser(user);
        }

    }

    public void removeFavoriteMosque(String userId, String mosqueId) {
        User user = findById(userId);
        List<String> favorites = user.getFavoriteMosques();

        if (favorites.contains(mosqueId)) {
            favorites.remove(mosqueId);
            user.setFavoriteMosques(favorites);
            saveUser(user);
        }

    }

    public User saveUser(User user) {
       return userRepository.save(user);
    }

    public void updateRole(Authentication authentication, UserDto userDto) {
       String email = authentication.getName();
       User user = findByEmail(email);
       if (user == null) {
           throw new RuntimeException("User not found");
       }

        user.setRoles(userDto.getRoles());
        saveUser(user);


    }

}