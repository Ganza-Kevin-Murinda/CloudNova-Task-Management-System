package com.taskmgt.CloudNova.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for user deletion operations
 */
@Setter
@Getter
@Schema(description = "Response returned after attempting to delete a user")
public class UserDeletionResponse {

    @Schema(description = "ID of the user that was deleted", example = "1")
    private Long userId;

    @Schema(description = "Indicates whether the deletion was successful", example = "true")
    private boolean deleted;

    @Schema(description = "Additional message regarding the deletion result", example = "User and all associated tasks successfully deleted")
    private String message;

    @Override
    public String toString() {
        return "UserDeletionResponse{" +
                "userId=" + userId +
                ", deleted=" + deleted +
                ", message='" + message + '\'' +
                '}';
    }
}
