package com.taskmgt.CloudNova.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for user existence checks
 */
@Setter
@Getter
@Schema(description = "Response for checking whether a user exists")
public class UserExistenceResponse {

    @Schema(description = "ID of the user being checked", example = "5")
    private Long userId;

    @Schema(description = "Indicates whether the user exists", example = "true")
    private boolean exists;

    @Schema(description = "Additional message about the existence check", example = "User exists")
    private String message;

    @Override
    public String toString() {
        return "UserExistenceResponse{" +
                "userId=" + userId +
                ", exists=" + exists +
                ", message='" + message + '\'' +
                '}';
    }
}
