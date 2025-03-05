//package com.mosquefinder.mapper;
//
//import com.mosquefinder.dto.UserDto;
//import com.mosquefinder.model.Location;
//import com.mosquefinder.model.User;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class UserMapper {
//
//    public UserDto toDto(User user) {
//        if (user == null) {
//            return null;
//        }
//
//        return UserDto.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .verified(user.isVerified())
//                .location((Location) user.getLocation())
//                .createdAt(user.getCreatedAt())
//                .lastLoginAt(user.getLastLoginAt())
//                .favoriteMosques(user.getFavoriteMosques())
//                .roles(user.getRoles().isEmpty() ? null : String.join(",", user.getRoles()))
//                .build();
//    }
//
//    public User toEntity(UserDto dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        Set<String> roles = new HashSet<>();
//        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
//            roles.addAll(List.of(dto.getRoles().split(",")));
//            return User.builder()
//                    .id(dto.getId())
//                    .name(dto.getName())
//                    .email(dto.getEmail())
//                    .verified(dto.isVerified())
////                    .location(dto.getLocation())
//                    .createdAt(dto.getCreatedAt())
//                    .lastLoginAt(dto.getLastLoginAt())
//                    .favoriteMosques(dto.getFavoriteMosques())
//                    .roles(roles)
//                    .build();
//        } else {
//            return User.builder()
//                    .id(dto.getId())
//                    .name(dto.getName())
//                    .email(dto.getEmail())
//                    .verified(dto.isVerified())
//                    .location(dto.getLocation())
//                    .createdAt(dto.getCreatedAt())
//                    .lastLoginAt(dto.getLastLoginAt())
//                    .favoriteMosques(dto.getFavoriteMosques())
//                    .roles(roles)
//                    .build();
//        }
//
//    }
//}
