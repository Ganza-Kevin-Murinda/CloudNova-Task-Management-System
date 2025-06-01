package com.taskmgt.CloudNova.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for task statistics
 */
@Setter
@Getter
@Schema(description = "Response object containing task statistics and counts")
public class TaskStatsResponse {

    @Schema(description = "Total number of tasks",
            example = "25")
    private long totalTasks;

    @Schema(description = "ID of the user for user-specific stats (null for global stats)",
            example = "1")
    private Long userId;

    @Schema(description = "Number of tasks with TODO status",
            example = "10")
    private long todoCount;

    @Schema(description = "Number of tasks with IN_PROGRESS status",
            example = "8")
    private long inProgressCount;

    @Schema(description = "Number of tasks with COMPLETED status",
            example = "7")
    private long completedCount;

    @Schema(hidden = true)
    @Override
    public String toString() {
        return "TaskStatsResponse{" +
                "totalTasks=" + totalTasks +
                ", userId=" + userId +
                ", todoCount=" + todoCount +
                ", inProgressCount=" + inProgressCount +
                ", completedCount=" + completedCount +
                '}';
    }
}