package com.taskmgt.CloudNova.service.impl;

import com.taskmgt.CloudNova.exception.DuplicateEmailException;
import com.taskmgt.CloudNova.exception.DuplicateUsernameException;
import com.taskmgt.CloudNova.exception.UserNotFoundException;
import com.taskmgt.CloudNova.model.User;
import com.taskmgt.CloudNova.repository.UserRepository;
import com.taskmgt.CloudNova.service.TaskService;
import com.taskmgt.CloudNova.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for User business logic
 * Handles all user-related operations with comprehensive validation and error handling
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TaskService taskService;

    @Override
    public User createUser(User user) {
        log.info("Starting user creation process for username: {}", user.getUsername());

        // Validate input
        validateUserInput(user, false);

        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Attempted to create user with duplicate email: {}", user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }

        // Check for duplicate username
        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Attempted to create user with duplicate username: {}", user.getUsername());
            throw new DuplicateUsernameException(user.getUsername());
        }

        try {
            User createdUser = userRepository.createUser(user);
            log.info("Successfully created user with ID: {} and username: {}",
                    createdUser.getId(), createdUser.getUsername());
            return createdUser;
        } catch (Exception e) {
            log.error("Error creating user with username {}: {}", user.getUsername(), e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Retrieving user by ID: {}", id);

        validateId(id);

        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            log.warn("User not found with ID: {}", id);
            throw new UserNotFoundException(id);
        }

        User user = userOptional.get();
        log.debug("Successfully retrieved user: {} with ID: {}", user.getUsername(), id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Retrieving all users");

        try {
            List<User> users = userRepository.findAll();
            log.info("Retrieved {} users from repository", users.size());
            return users;
        } catch (Exception e) {
            log.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve users: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        log.info("Starting user update process for ID: {}", id);

        validateId(id);
        validateUserInput(user, true);

        // Check if user exists
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to update non-existent user with ID: {}", id);
            throw new UserNotFoundException(id);
        }

        // Check for duplicate email (excluding current user)
        Optional<User> existingUserWithEmail = userRepository.findByEmail(user.getEmail());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
            log.warn("Attempted to update user {} with duplicate email: {}", id, user.getEmail());
            throw new DuplicateEmailException(user.getEmail());
        }

        // Check for duplicate username (excluding current user)
        Optional<User> existingUserWithUsername = userRepository.findByUsername(user.getUsername());
        if (existingUserWithUsername.isPresent() && !existingUserWithUsername.get().getId().equals(id)) {
            log.warn("Attempted to update user {} with duplicate username: {}", id, user.getUsername());
            throw new DuplicateUsernameException(user.getUsername());
        }

        try {
            // Set the ID to ensure we're updating the correct user
            user.setId(id);
            User updatedUser = userRepository.updateUser(user);
            log.info("Successfully updated user with ID: {} and username: {}",
                    updatedUser.getId(), updatedUser.getUsername());
            return updatedUser;
        } catch (Exception e) {
            log.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(Long id) {
        log.info("Starting user deletion process for ID: {}", id);

        validateId(id);

        // Check if user exists
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent user with ID: {}", id);
            throw new UserNotFoundException(id);
        }

        try {
            // Check if user has associated tasks
            long taskCount = taskService.getTaskCountByUserId(id);

            if (taskCount > 0) {
                log.info("User with ID: {} has {} associated tasks. Cleaning up tasks before deletion.", id, taskCount);

                // Delete all tasks associated with this user
                long deletedTaskCount = taskService.deleteAllTasksByUserId(id);

                if (deletedTaskCount != taskCount) {
                    log.warn("Expected to delete {} tasks but only deleted {} tasks for user ID: {}",
                            taskCount, deletedTaskCount, id);
                } else {
                    log.info("Successfully deleted {} tasks for user ID: {}", deletedTaskCount, id);
                }
            } else {
                log.debug("User with ID: {} has no associated tasks", id);
            }

            // Now delete the user
            boolean deleted = userRepository.deleteById(id);

            if (deleted) {
                log.info("Successfully deleted user with ID: {} and {} associated tasks", id, taskCount);
            } else {
                log.warn("Failed to delete user with ID: {} - user may not exist", id);
            }

            return deleted;
        } catch (UserNotFoundException e) {
            // Re-throw UserNotFoundException as is
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        log.debug("Retrieving user by username: {}", username);

        validateUsername(username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.warn("User not found with username: {}", username);
            throw new UserNotFoundException("username", username);
        }

        User user = userOptional.get();
        log.debug("Successfully retrieved user: {} with username: {}", user.getId(), username);
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        log.debug("Retrieving user by email: {}", email);

        validateEmail(email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.warn("User not found with email: {}", email);
            throw new UserNotFoundException("email", email);
        }

        User user = userOptional.get();
        log.debug("Successfully retrieved user: {} with email: {}", user.getId(), email);
        return user;
    }

    @Override
    public List<User> getUsersByFirstName(String firstName) {
        log.debug("Retrieving users by first name: {}", firstName);

        validateFirstName(firstName);

        try {
            List<User> users = userRepository.findByFirstNameContaining(firstName);
            log.debug("Found {} users with first name containing: {}", users.size(), firstName);
            return users;
        } catch (Exception e) {
            log.error("Error retrieving users by first name {}: {}", firstName, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve users by first name: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean userExists(Long id) {
        log.debug("Checking if user exists with ID: {}", id);

        if (id == null) {
            return false;
        }

        try {
            boolean exists = userRepository.existsById(id);
            log.debug("User with ID {} exists: {}", id, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking user existence for ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public long getUserCount() {
        log.debug("Getting total user count");

        try {
            long count = userRepository.count();
            log.debug("Total user count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Error getting user count: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get user count: " + e.getMessage(), e);
        }
    }

    // ─── Private Validation Methods ─────────────────────────────────────

    private void validateUserInput(User user, boolean isUpdate) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());

        // For updates, ID should not be set manually (we set it in the service)
        if (!isUpdate && user.getId() != null) {
            throw new IllegalArgumentException("ID should not be provided when creating a new user");
        }
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        // Basic email validation (Spring's @Email annotation will handle more complex validation)
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email format is invalid");
        }
    }

    private void validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
    }

    private void validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
    }
}