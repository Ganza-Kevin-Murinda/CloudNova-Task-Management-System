package com.taskmgt.CloudNova.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for user statistics
 */
@Setter
@Getter
@Schema(description = "Response containing statistics about the users")
public class UserStatsResponse {

    @Schema(description = "Total number of registered users", example = "123")
    private long totalUsers;

    @Schema(description = "Additional message about the stats", example = "Total registered users: 123")
    private String message;

    @Override
    public String toString() {
        return "UserStatsResponse{" +
                "totalUsers=" + totalUsers +
                ", message='" + message + '\'' +
                '}';
    }
}
