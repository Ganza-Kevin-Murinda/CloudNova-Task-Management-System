package com.taskmgt.CloudNova.DTO;

import com.taskmgt.CloudNova.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO for user search operations
 */
@Getter
@Setter
@Schema(description = "Response for user search operations by username, email, or first name")
public class UserSearchResponse {

    @Schema(description = "List of users matching the search criteria")
    private List<User> users = new ArrayList<>();

    @Schema(description = "Indicates whether the search was successful", example = "true")
    private boolean success;

    @Schema(description = "Additional message about the search result", example = "Search completed successfully")
    private String message;

    @Schema(description = "The criteria used for the search", example = "username: kevin")
    private String searchCriteria;

    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public String toString() {
        return "UserSearchResponse{" +
                "users=" + users.size() + " users" +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", searchCriteria='" + searchCriteria + '\'' +
                '}';
    }
}
