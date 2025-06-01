package com.taskmgt.CloudNova.controller;

import com.taskmgt.CloudNova.DTO.UserDeletionResponse;
import com.taskmgt.CloudNova.DTO.UserExistenceResponse;
import com.taskmgt.CloudNova.DTO.UserSearchResponse;
import com.taskmgt.CloudNova.DTO.UserStatsResponse;
import com.taskmgt.CloudNova.model.User;
import com.taskmgt.CloudNova.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Controller", description = "Handles user management operations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user input")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Creating new user with username: {} and email: {}", user.getUsername(), user.getEmail());
        log.debug("User details: {}", user);
        User createdUser = userService.createUser(user);
        log.info("Successfully created user with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        log.info("Retrieved {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userService.getUserById(id);
        log.info("Successfully retrieved user with ID: {}", user.getId());
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        log.info("Updating user with ID: {}", id);
        User updatedUser = userService.updateUser(id, user);
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Failed to delete user")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDeletionResponse> deleteUser(@PathVariable Long id) {
        log.info("Initiating deletion process for user with ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        UserDeletionResponse response = new UserDeletionResponse();
        response.setUserId(id);
        response.setDeleted(deleted);
        response.setMessage(deleted ? "User and all associated tasks successfully deleted"
                : "Failed to delete user");
        return deleted
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @Operation(summary = "Get user by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user with username: {}", username);
        User user = userService.getUserByUsername(username);
        log.info("Successfully retrieved user with username: {}", username);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get user by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user with email: {}", email);
        User user = userService.getUserByEmail(email);
        log.info("Successfully retrieved user with email: {}", email);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Search users by first name (partial match)")
    @ApiResponse(responseCode = "200", description = "Users found")
    @GetMapping("/search/firstname")
    public ResponseEntity<List<User>> getUsersByFirstName(@RequestParam String firstName) {
        log.info("Searching users with first name: {}", firstName);
        List<User> users = userService.getUsersByFirstName(firstName);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Check if user exists by ID")
    @ApiResponse(responseCode = "200", description = "User existence checked")
    @GetMapping("/{id}/exists")
    public ResponseEntity<UserExistenceResponse> checkUserExists(@PathVariable Long id) {
        log.info("Checking existence of user with ID: {}", id);
        boolean exists = userService.userExists(id);
        UserExistenceResponse response = new UserExistenceResponse();
        response.setUserId(id);
        response.setExists(exists);
        response.setMessage(exists ? "User exists" : "User does not exist");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user statistics")
    @ApiResponse(responseCode = "200", description = "User stats retrieved")
    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        log.info("Fetching user statistics");
        long totalUsers = userService.getUserCount();
        UserStatsResponse stats = new UserStatsResponse();
        stats.setTotalUsers(totalUsers);
        stats.setMessage("Total registered users: " + totalUsers);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Search users by username, email, or first name")
    @ApiResponse(responseCode = "200", description = "Users found")
    @GetMapping("/search")
    public ResponseEntity<UserSearchResponse> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String firstName) {

        log.info("Searching users - Username: {}, Email: {}, FirstName: {}", username, email, firstName);
        UserSearchResponse response = new UserSearchResponse();

        try {
            if (username != null && !username.trim().isEmpty()) {
                response.addUser(userService.getUserByUsername(username));
                response.setSearchCriteria("username: " + username);
            } else if (email != null && !email.trim().isEmpty()) {
                response.addUser(userService.getUserByEmail(email));
                response.setSearchCriteria("email: " + email);
            } else if (firstName != null && !firstName.trim().isEmpty()) {
                response.setUsers(userService.getUsersByFirstName(firstName));
                response.setSearchCriteria("firstName: " + firstName);
            } else {
                response.setUsers(userService.getAllUsers());
                response.setSearchCriteria("all users");
            }

            response.setSuccess(true);
            response.setMessage("Search completed successfully");

        } catch (Exception e) {
            log.warn("Search failed: {}", e.getMessage());
            response.setSuccess(false);
            response.setMessage("No users found matching the search criteria");
            response.setSearchCriteria(String.format("username: %s, email: %s, firstName: %s", username, email, firstName));
        }

        return ResponseEntity.ok(response);
    }
}
