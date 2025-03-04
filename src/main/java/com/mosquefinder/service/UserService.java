package com.mosquefinder.service;

import com.mosquefinder.dto.UserDto;
import com.mosquefinder.exception.ResourceNotFoundException;
import com.mosquefinder.model.Locations;
import com.mosquefinder.model.User;
import com.mosquefinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(String name, String email, String encodedPassword) {
        User user = User.builder()
                .name(name)
                .email(email)
                .password(encodedPassword)
                .verified(false)
                .roles(Set.of("USER"))
                .createdAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
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

    public User verifyUser(String email) {
        User user = findByEmail(email);
        user.setVerified(true);
        return userRepository.save(user);
    }


    public User updateLoginTime(String email) {
        User user = findByEmail(email);
        user.setLastLoginAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateLocation(String userId, Locations location) {
        User user = findById(userId);
        user.setLocation(location);
        return userRepository.save(user);
    }

    public User addFavoriteMosque(String userId, String mosqueId) {
        User user = findById(userId);
        List<String> favorites = user.getFavoriteMosques();

        if (!favorites.contains(mosqueId)) {
            favorites.add(mosqueId);
            user.setFavoriteMosques(favorites);
            return userRepository.save(user);
        }

        return user;
    }

    public User removeFavoriteMosque(String userId, String mosqueId) {
        User user = findById(userId);
        List<String> favorites = user.getFavoriteMosques();

        if (favorites.contains(mosqueId)) {
            favorites.remove(mosqueId);
            user.setFavoriteMosques(favorites);
            return userRepository.save(user);
        }

        return user;
    }

    public String convertToDto(User user) {
        return String.valueOf(UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .verified(user.isVerified())
                .location((Locations) user.getLocation())
                .favoriteMosques(user.getFavoriteMosques())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build());
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}