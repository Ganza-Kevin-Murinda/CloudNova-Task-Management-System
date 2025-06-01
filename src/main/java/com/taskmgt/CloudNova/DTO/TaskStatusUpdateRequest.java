package com.taskmgt.CloudNova.DTO;

import com.taskmgt.CloudNova.model.ETaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for updating task status
 */
@Setter
@Getter
@Schema(description = "Request object for updating a task's status")
public class TaskStatusUpdateRequest {

    @Schema(description = "New status for the task",
            example = "IN_PROGRESS",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"TODO", "IN_PROGRESS", "COMPLETED"})
    private ETaskStatus status;
}