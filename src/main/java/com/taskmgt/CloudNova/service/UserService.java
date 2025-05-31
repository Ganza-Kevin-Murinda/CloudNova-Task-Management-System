package com.taskmgt.CloudNova.service;

import com.taskmgt.CloudNova.exception.DuplicateEmailException;
import com.taskmgt.CloudNova.exception.DuplicateUsernameException;
import com.taskmgt.CloudNova.exception.UserNotFoundException;
import com.taskmgt.CloudNova.model.User;

import java.util.List;

/**
 * Service interface for User operations
 * Defines business logic methods for user management
 */
public interface UserService {

    /**
     * Create a new user with validation
     * @param user the user to create
     * @return the created user with generated ID
     * @throws DuplicateEmailException if email already exists
     * @throws DuplicateUsernameException if username already exists
     * @throws IllegalArgumentException if user data is invalid
     */
    User createUser(User user);

    /**
     * Get user by ID
     * @param id the user ID
     * @return the user if found
     * @throws UserNotFoundException if user not found
     * @throws IllegalArgumentException if ID is null or invalid
     */
    User getUserById(Long id);

    /**
     * Get all users
     * @return list of all users (empty list if none found)
     */
    List<User> getAllUsers();

    /**
     * Update an existing user
     * @param id the user ID to update
     * @param user the updated user data
     * @return the updated user
     * @throws UserNotFoundException if user not found
     * @throws DuplicateEmailException if email already exists for another user
     * @throws DuplicateUsernameException if username already exists for another user
     * @throws IllegalArgumentException if data is invalid
     */
    User updateUser(Long id, User user);

    /**
     * Delete user by ID
     * @param id the user ID to delete
     * @return true if user was deleted successfully
     * @throws UserNotFoundException if user not found
     * @throws IllegalArgumentException if ID is null or invalid
     */
    boolean deleteUser(Long id);

    /**
     * Get user by username
     * @param username the username to search for
     * @return the user if found
     * @throws UserNotFoundException if user not found
     * @throws IllegalArgumentException if username is null or empty
     */
    User getUserByUsername(String username);

    /**
     * Get user by email
     * @param email the email to search for
     * @return the user if found
     * @throws UserNotFoundException if user not found
     * @throws IllegalArgumentException if email is null or empty
     */
    User getUserByEmail(String email);

    /**
     * Find users by first name (partial match)
     * @param firstName the first name to search for
     * @return list of matching users (empty list if none found)
     * @throws IllegalArgumentException if firstName is null or empty
     */
    List<User> getUsersByFirstName(String firstName);

    /**
     * Check if user exists by ID
     * @param id the user ID to check
     * @return true if user exists, false otherwise
     */
    boolean userExists(Long id);

    /**
     * Get total count of users
     * @return the number of users
     */
    long getUserCount();
}