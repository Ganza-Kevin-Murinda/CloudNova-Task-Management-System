package com.taskmgt.CloudNova.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for task deletion operations
 */
@Setter
@Getter
@Schema(description = "Response object containing details about task deletion operation")
public class TaskDeletionResponse {

    @Schema(description = "Number of tasks that were deleted",
            example = "5")
    private long deletedCount;

    @Schema(description = "ID of the user whose tasks were deleted",
            example = "1")
    private Long userId;

    @Schema(description = "Descriptive message about the deletion operation",
            example = "Successfully deleted 5 tasks for user 1")
    private String message;
}