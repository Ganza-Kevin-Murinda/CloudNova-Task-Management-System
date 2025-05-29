package com.taskmgt.CloudNova.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Status is required")
    private ETaskStatus status;

    @NotNull(message = "Priority is required")
    private EPriority priority;

    @NotNull(message = "User ID is required")
    private Long userId;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // Helper method to set timestamps when creating
    public void setCreationTimestamp() {
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // Helper method to update timestamp when modifying
    public void setUpdateTimestamp() {
        this.updatedDate = LocalDateTime.now();
    }
}
