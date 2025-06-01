package com.taskmgt.CloudNova.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Task entity representing a task in the task management system")
public class Task {

    @Schema(description = "Unique identifier for the task",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "Task title/name",
            example = "Complete project documentation",
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Detailed description of the task",
            example = "Write comprehensive documentation for the task management API including all endpoints and examples",
            maxLength = 500,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "Status is required")
    @Schema(description = "Current status of the task",
            example = "TODO",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"TODO", "IN_PROGRESS", "COMPLETED"})
    private ETaskStatus status;

    @NotNull(message = "Priority is required")
    @Schema(description = "Priority level of the task",
            example = "HIGH",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    private EPriority priority;

    @NotNull(message = "User ID is required")
    @Schema(description = "ID of the user who owns this task",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "Timestamp when the task was created",
            example = "2024-01-15T10:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdDate;

    @Schema(description = "Timestamp when the task was last updated",
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