package com.taskmgt.CloudNova.DTO;

import com.taskmgt.CloudNova.model.EPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating task priority
 */
@Setter
@Getter
@Schema(description = "Request object for updating a task's priority level")
public class TaskPriorityUpdateRequest {

    @Schema(description = "New priority level for the task",
            example = "HIGH",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"LOW", "MEDIUM", "HIGH", "URGENT"})
    private EPriority priority;
}