package com.taskmgt.CloudNova.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User entity representing a registered user in the task management system")
public class User {

    @Schema(description = "Unique identifier for the user",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Unique username for the user",
            example = "john_doe",
            minLength = 3,
            maxLength = 50,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "User's email address",
            example = "john.doe@example.com",
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name",
            example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name",
            example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "Timestamp when the user was created",
            example = "2024-01-15T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdDate;

    @Schema(description = "Timestamp when the user was last updated",
            example = "2024-01-20T14:45:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedDate;

    // Helper method to set timestamps when creating
    @Schema(hidden = true)
    public void setCreationTimestamp() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // Helper method to update timestamp when modifying
    @Schema(hidden = true)
    public void setUpdateTimestamp() {
        this.updatedDate = LocalDateTime.now();
    }
}