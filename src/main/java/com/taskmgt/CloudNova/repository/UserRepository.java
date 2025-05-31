package com.taskmgt.CloudNova.repository;

import com.taskmgt.CloudNova.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class UserRepository {

    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // ─── Core CRUD ───────────────────────────────────────────────────

    /**
     * Save a user (CREATE operation)
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    public User createUser(User user) {
        log.debug("Creating user: {}", user.getUsername());

        validateIdNotExists(user.getId());

        Long newId = idGenerator.getAndIncrement();
        user.setId(newId);
        user.setCreationTimestamp();

        users.put(user.getId(), user);
        log.info("User created with ID: {}", user.getId());
        return user;
    }

    /**
     * Find all users
     * @return List of all users
     */
    public List<User> findAll() {
        log.debug("Finding all users");

        try {
            List<User> allUsers = new ArrayList<>(users.values());
            log.debug("Found {} users", allUsers.size());
            return allUsers;
        } catch (Exception e) {
            log.error("Error finding all users: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Delete user by ID
     * @param id the user ID to delete
     * @return true if user was deleted, false if not found
     */
    public boolean deleteById(Long id) {
        log.debug("Attempting to delete user by ID: {}", id);

        validateIdExists(id);

        try {
            User removedUser = users.remove(id);
            boolean deleted = removedUser != null;

            if (deleted) {
                log.info("Successfully deleted user: {} with ID: {}", removedUser.getUsername(), id);
            } else {
                log.debug("No user found to delete with ID: {}", id);
            }

            return deleted;
        } catch (Exception e) {
            log.error("Error deleting user by ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Update a user (UPDATE operation)
     * @param user the user to save
     * @return the updated user
     */
    public User updateUser(User user) {
        log.debug("Updating user with ID: {}", user.getId());

        validateIdExists(user.getId());

        user.setUpdateTimestamp();
        users.put(user.getId(), user);
        log.info("User updated with ID: {}", user.getId());
        return user;
    }

    /**
     * Get total count of users
     * @return the number of users
     */
    public long count() {
        log.debug("Getting user count");

        try {
            long userCount = users.size();
            log.debug("Total users count: {}", userCount);
            return userCount;
        } catch (Exception e) {
            log.error("Error getting user count: {}", e.getMessage(), e);
            return 0;
        }
    }

    // ─── Search & Queries ────────────────────────────────────────────

    /**
     * Find user by ID
     * @param id the user ID
     * @return Optional containing the user if found
     */
    public Optional<User> findById(Long id) {
        log.debug("Finding user by ID: {}", id);

        if (id == null) {
            log.warn("Cannot find user with null ID");
            return Optional.empty();
        }

        try {
            User user = users.get(id);
            if (user != null) {
                log.debug("Found user: {} with ID: {}", user.getUsername(), id);
                return Optional.of(user);
            } else {
                log.debug("No user found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error finding user by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);

        if (username == null || username.trim().isEmpty()) {
            log.warn("Cannot find user with null or empty username");
            return Optional.empty();
        }

        try {
            Optional<User> user = users.values().stream()
                    .filter(u -> username.equals(u.getUsername()))
                    .findFirst();

            if (user.isPresent()) {
                log.debug("Found user with username: {}", username);
            } else {
                log.debug("No user found with username: {}", username);
            }

            return user;
        } catch (Exception e) {
            log.error("Error finding user by username {}: {}", username, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Find user by email
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            log.warn("Cannot find user with null or empty email");
            return Optional.empty();
        }

        try {
            Optional<User> user = users.values().stream()
                    .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                    .findFirst();

            if (user.isPresent()) {
                log.debug("Found user with email: {}", email);
            } else {
                log.debug("No user found with email: {}", email);
            }

            return user;
        } catch (Exception e) {
            log.error("Error finding user by email {}: {}", email, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Find users by first name (partial match, case-insensitive)
     * @param firstName the first name to search for
     * @return List of users matching the first name
     */
    public List<User> findByFirstNameContaining(String firstName) {
        log.debug("Finding users by first name containing: {}", firstName);

        if (firstName == null || firstName.trim().isEmpty()) {
            log.warn("Cannot search for users with null or empty first name");
            return new ArrayList<>();
        }

        try {
            List<User> matchingUsers = users.values().stream()
                    .filter(user -> user.getFirstName() != null &&
                            user.getFirstName().toLowerCase().contains(firstName.toLowerCase()))
                    .collect(Collectors.toList());

            log.debug("Found {} users with first name containing: {}", matchingUsers.size(), firstName);
            return matchingUsers;
        } catch (Exception e) {
            log.error("Error finding users by first name {}: {}", firstName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ─── Validations ─────────────────────────────────────────────────

    /**
     * Check if user exists by ID
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     */
    public boolean existsById(Long id) {
        log.debug("Checking if user exists by ID: {}", id);

        if (id == null) {
            log.warn("Cannot check existence for null ID");
            return false;
        }

        try {
            boolean exists = users.containsKey(id);
            log.debug("User with ID {} exists: {}", id, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking user existence by ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if the username already exists (for validation)
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        log.debug("Checking if username exists: {}", username);

        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        try {
            boolean exists = users.values().stream()
                    .anyMatch(user -> username.equals(user.getUsername()));
            log.debug("Username {} exists: {}", username, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking username existence {}: {}", username, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if email already exists (for validation)
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        log.debug("Checking if email exists: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            boolean exists = users.values().stream()
                    .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));
            log.debug("Email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking email existence {}: {}", email, e.getMessage(), e);
            return false;
        }
    }

    private void validateIdExists(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID " + id + " does not exist");
        }
    }

    private void validateIdNotExists(Long id) {
        if (id != null && users.containsKey(id)) {
            throw new IllegalArgumentException("User with ID already exists: " + id);
        }
    }


    /**
     * Clear all users (for testing purposes)
     */
    public void deleteAll() {
        log.warn("Deleting all users from repository");
        try {
            int count = users.size();
            users.clear();
            idGenerator.set(1);
            log.info("Successfully deleted {} users", count);
        } catch (Exception e) {
            log.error("Error deleting all users: {}", e.getMessage(), e);
        }
    }
}
