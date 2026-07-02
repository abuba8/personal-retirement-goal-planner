package com.skillstorm.retirementplanner.services;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skillstorm.retirementplanner.dtos.UpdateProfileDto;
import com.skillstorm.retirementplanner.dtos.UserDto;
import com.skillstorm.retirementplanner.mappers.UserMapper;
import com.skillstorm.retirementplanner.models.User;
import com.skillstorm.retirementplanner.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    /**
     * UserServiceTest Class:
     * Unit tests for UserService's business logic, in isolation.
     * Repository, mapper and password encoder are mocked (no real DB / hashing).
     *
     * Tests:
     * 
     * getCurrentUserProfile():
     * - returnsProfileWhenUserExists; 200 + profile
     * - returnsNotFoundWhenUserMissing: 404
     *
     * updateProfile():
     * - returnsOkWhenEmailUnique: 200 + saves
     * - returnsConflictWhenUsernameTaken: 409 + never saves
     * - returnsConflictWhenEmailTaken: 409 + never saves
     * - hashesNewPassword: 200 + encodes password + saves
     * - returnsNotFoundWhenUserMissing: 404 + never encodes/saves
     */
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    // real service under test with the mocks injected
    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testDto;
    
    @BeforeEach
    void dataInit(){
        testUser = new User(1L, "alice", "alice@example.com", "oldHash123");
        testDto = new UserDto(1L, "alice", "alice@example.com");
    }

    @Nested
    @DisplayName("getCurrentUserProfile()")
    class GetCurrentUserProfile{
        /**
         * Existing user: 200 with the mapped DTO.
         *
         * setup:
         * - findById(1): user; toDto -> dto
         *
         * assert:
         * - status 200, body is the dto, findById was called
         */
        @Test
        @DisplayName("returns 200 OK with the profile when the user exists")
        void returnsProfileWhenUserExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userMapper.toDto(testUser)).thenReturn(testDto);
 
            ResponseEntity<UserDto> results = userService.getCurrentUserProfile(1L);
 
            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testDto, results.getBody());
 
            verify(userRepository).findById(1L);
        }
 
        // no such user -> 404
        @Test
        @DisplayName("returns 404 NOT FOUND when the user is missing")
        void returnsNotFoundWhenUserMissing(){
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
 
            ResponseEntity<UserDto> results = userService.getCurrentUserProfile(99L);
 
            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());
 
            verify(userRepository).findById(99L);
        }
    }
    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {
        /**
         * New email that isn't taken: updated and saved.
         *
         * setup:
         * - findById(1) -> user; existsByEmail(new) -> false; save -> user; toDto -> dto
         *
         * assert:
         * - status 200, body is the dto, save was called
         */
        @Test
        @DisplayName("returns 200 OK and updates the email when it is unique")
        void returnsOkWhenEmailUnique() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDto(any(User.class))).thenReturn(testDto);
 
            UpdateProfileDto dto = new UpdateProfileDto(null, "new@example.com", null);
            ResponseEntity<UserDto> results = userService.updateProfile(1L, dto);
 
            assertEquals(HttpStatus.OK, results.getStatusCode());
            assertEquals(testDto, results.getBody());
 
            verify(userRepository).save(any(User.class));
        }
 
        // if new username exists -> 409 and nothings saved
        @Test
        @DisplayName("returns 409 CONFLICT and never saves when the new username is taken")
        void returnsConflictWhenUsernameTaken() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("takenName")).thenReturn(true);
 
            UpdateProfileDto dto = new UpdateProfileDto("takenName", null, null);
            ResponseEntity<UserDto> results = userService.updateProfile(1L, dto);
 
            assertEquals(HttpStatus.CONFLICT, results.getStatusCode());
            assertNull(results.getBody());
 
            verify(userRepository, never()).save(any(User.class));
        }
 
        // new email already exists -> 409 and nothigns saved
        @Test
        @DisplayName("returns 409 CONFLICT and never saves when the new email is taken")
        void returnsConflictWhenEmailTaken() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);
 
            UpdateProfileDto dto = new UpdateProfileDto(null, "taken@example.com", null);
            ResponseEntity<UserDto> results = userService.updateProfile(1L, dto);
 
            assertEquals(HttpStatus.CONFLICT, results.getStatusCode());
            assertNull(results.getBody());
 
            verify(userRepository, never()).save(any(User.class));
        }
 
        // provided password is hashed (never stored raw) before saving
        @Test
        @DisplayName("returns 200 OK and hashes the new password when one is provided")
        void hashesNewPassword() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("newPassword123")).thenReturn("newHash");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDto(any(User.class))).thenReturn(testDto);
 
            UpdateProfileDto dto = new UpdateProfileDto(null, null, "newPassword123");
            ResponseEntity<UserDto> results = userService.updateProfile(1L, dto);
 
            assertEquals(HttpStatus.OK, results.getStatusCode());
 
            verify(passwordEncoder).encode("newPassword123");
            verify(userRepository).save(any(User.class));
        }
 
        // no such user -> 404 and neither hashing nor saving happens
        @Test
        @DisplayName("returns 404 NOT FOUND and never hashes when the user is missing")
        void returnsNotFoundWhenUserMissing() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
 
            UpdateProfileDto dto = new UpdateProfileDto(null, "x@example.com", null);
            ResponseEntity<UserDto> results = userService.updateProfile(99L, dto);
 
            assertEquals(HttpStatus.NOT_FOUND, results.getStatusCode());
            assertNull(results.getBody());
 
            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }
}