package com.skillstorm.retirementplanner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillstorm.retirementplanner.dtos.UpdateProfileDto;
import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.mappers.UserMapper;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@Service
public class UserService {
    /**
     * Service Class: All Business logic is implemented here for User Entity 
     * For each method an appropriate response entities is returned to the controller layer.
     * 
     * Methods:
     * getCurrentUserProfile(Long userId)
     * updateProfile(Long userId, UpdateProfileDto dto)
     */
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // parameterized constructor
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // get all for testing 
    public ResponseEntity<List<User>> getAllUserProfile(){
        return ResponseEntity.ok(this.userRepository.findAll(Sort.by("id").ascending()));
    }

    /**
     * getCurrentUserProfile:
     * Fetch the current user, by finding the userId.
     * 
     * args: 
     * - Long userId: current user
     * 
     * return: 
     * - 404: if the object is empty
     * - 200: if successfully found it
     */
    public ResponseEntity<UserDto> getCurrentUserProfile(Long userId){
        Optional<User> userObj = this.userRepository.findById(userId);
        if (userObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(this.userMapper.toDto(userObj.get()));
    }

    /**
     * updateProfile:
     * Update the current user profile
     * 
     * args: 
     * - Long userId: current user
     * - UpdateProfileDto dto: data transfer object for udpating profile
     * 
     * return:
     * - 404: if object's empty
     * - 409: conflict if username or email already exists
     * - 200: if successfully updated
     */
    public ResponseEntity<UserDto> updateProfile(Long userId, UpdateProfileDto dto) {
        Optional<User> userObj = this.userRepository.findById(userId);
        if(userObj.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        User user = userObj.get();

        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            if (this.userRepository.existsByUsername(dto.username())) {
                return ResponseEntity.status(409).build();
            }
            user.setUsername(dto.username());
        }

        if(dto.email()!= null && !dto.email().equals(user.getEmail())){
            if(this.userRepository.existsByEmail(dto.email())){
                return ResponseEntity.status(409).build();
            }
            user.setEmail(dto.email());
        }

        if(dto.password() != null && !dto.password().isBlank()){
            user.setPasswordHash(this.passwordEncoder.encode(dto.password()));
        }

        User saved = this.userRepository.save(user);
        return ResponseEntity.ok(this.userMapper.toDto(saved));
    }
    
}
