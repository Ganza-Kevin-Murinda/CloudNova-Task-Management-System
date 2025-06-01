package com.taskmgt.CloudNova.controller;

import com.taskmgt.CloudNova.DTO.TaskDeletionResponse;
import com.taskmgt.CloudNova.DTO.TaskPriorityUpdateRequest;
import com.taskmgt.CloudNova.DTO.TaskStatsResponse;
import com.taskmgt.CloudNova.DTO.TaskStatusUpdateRequest;
import com.taskmgt.CloudNova.model.Task;
import com.taskmgt.CloudNova.model.ETaskStatus;
import com.taskmgt.CloudNova.model.EPriority;
import com.taskmgt.CloudNova.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task management operations
 * Provides endpoints for CRUD operations and advanced filtering
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Task Controller", description = "Handles task management operations including CRUD, filtering, and statistics")
public class TaskController {

    private final TaskService taskService;


    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid task input")
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        log.info("Creating new task with title: {}", task.getTitle());
        log.debug("Task details: {}", task);

        Task createdTask = taskService.createTask(task);

        log.info("Successfully created task with ID: {}", createdTask.getId());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }


    @Operation(summary = "Get all tasks with optional filtering",
            description = "Retrieve all tasks or filter by status, priority, and/or user ID")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @Parameter(description = "Filter by task status") @RequestParam(required = false) ETaskStatus status,
            @Parameter(description = "Filter by task priority") @RequestParam(required = false) EPriority priority,
            @Parameter(description = "Filter by user ID") @RequestParam(required = false) Long userId) {

        log.info("Fetching tasks with filters - Status: {}, Priority: {}, UserId: {}",
                status, priority, userId);

        List<Task> tasks;

        // Apply filtering based on query parameters
        if (userId != null && status != null) {
            log.debug("Filtering by userId and status");
            tasks = taskService.getTasksByUserAndStatus(userId, status);
        } else if (userId != null && priority != null) {
            log.debug("Filtering by userId and priority");
            tasks = taskService.getTasksByUserAndPriority(userId, priority);
        } else if (userId != null) {
            log.debug("Filtering by userId only");
            tasks = taskService.getTasksByUserId(userId);
        } else if (status != null) {
            log.debug("Filtering by status only");
            tasks = taskService.getTasksByStatus(status);
        } else if (priority != null) {
            log.debug("Filtering by priority only");
            tasks = taskService.getTasksByPriority(priority);
        } else {
            log.debug("No filters applied, fetching all tasks");
            tasks = taskService.getAllTasks();
        }

        log.info("Retrieved {} tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }


    @Operation(summary = "Get task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@Parameter(description = "Task ID") @PathVariable Long id) {
        log.info("Fetching task with ID: {}", id);

        Task task = taskService.getTaskById(id);

        log.info("Successfully retrieved task with ID: {}", id);
        log.debug("Task's details: {}", task);
        return ResponseEntity.ok(task);
    }


    @Operation(summary = "Update task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody Task task) {
        log.info("Updating task with ID: {}", id);
        log.debug("Updated task details: {}", task);

        Task updatedTask = taskService.updateTask(id, task);

        log.info("Successfully updated task with ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }


    @Operation(summary = "Update task status",
            description = "Update only the status of a specific task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status data")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestBody TaskStatusUpdateRequest request) {
        log.info("Updating status for task ID: {} to status: {}", id, request.getStatus());

        Task updatedTask = taskService.updateTaskStatus(id, request.getStatus());

        log.info("Successfully updated task status for ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }


    @Operation(summary = "Update task priority",
            description = "Update only the priority of a specific task")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task priority updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid priority data")
    })
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Task> updateTaskPriority(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestBody TaskPriorityUpdateRequest request) {
        log.info("Updating priority for task ID: {} to priority: {}", id, request.getPriority());

        Task updatedTask = taskService.updateTaskPriority(id, request.getPriority());

        log.info("Successfully updated task priority for ID: {}", id);
        return ResponseEntity.ok(updatedTask);
    }


    @Operation(summary = "Delete task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID") @PathVariable Long id) {
        log.info("Deleting task with ID: {}", id);

        taskService.deleteTask(id);

        log.info("Successfully deleted task with ID: {}", id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Get tasks by user ID with optional filtering",
            description = "Retrieve tasks for a specific user with optional filters: completed, pending, or high-priority")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getTasksByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Filter type: completed, pending, or high-priority") @RequestParam(required = false) String filter) {
        log.info("Fetching tasks for user ID: {} with filter: {}", userId, filter);

        List<Task> tasks;

        if ("completed".equalsIgnoreCase(filter)) {
            tasks = taskService.getCompletedTasksByUserId(userId);
        } else if ("pending".equalsIgnoreCase(filter)) {
            tasks = taskService.getPendingTasksByUserId(userId);
        } else if ("high-priority".equalsIgnoreCase(filter)) {
            tasks = taskService.getHighPriorityTasksByUserId(userId);
        } else {
            tasks = taskService.getTasksByUserId(userId);
        }

        log.info("Retrieved {} tasks for user ID: {}", tasks.size(), userId);
        return ResponseEntity.ok(tasks);
    }


    @Operation(summary = "Search tasks by title",
            description = "Find tasks containing the specified title text")
    @ApiResponse(responseCode = "200", description = "Tasks found")
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasksByTitle(
            @Parameter(description = "Title text to search for") @RequestParam String title) {
        log.info("Searching tasks with title containing: {}", title);

        List<Task> tasks = taskService.searchTasksByTitle(title);

        log.info("Found {} tasks matching title search: {}", tasks.size(), title);
        return ResponseEntity.ok(tasks);
    }


    @Operation(summary = "Get tasks sorted by creation date",
            description = "Retrieve all tasks ordered by their creation date")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved and sorted successfully")
    @GetMapping("/sorted/created-date")
    public ResponseEntity<List<Task>> getTasksSortedByCreatedDate() {
        log.info("Fetching all tasks sorted by creation date");

        List<Task> tasks = taskService.getAllTasksSortedByCreatedDate();

        log.info("Retrieved {} tasks sorted by creation date", tasks.size());
        return ResponseEntity.ok(tasks);
    }


    @Operation(summary = "Get user's tasks sorted by priority",
            description = "Retrieve tasks for a specific user ordered by priority level")
    @ApiResponse(responseCode = "200", description = "Tasks retrieved and sorted successfully")
    @GetMapping("/user/{userId}/sorted/priority")
    public ResponseEntity<List<Task>> getTasksByUserIdSortedByPriority(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Fetching tasks for user ID: {} sorted by priority", userId);

        List<Task> tasks = taskService.getTasksByUserIdSortedByPriority(userId);

        log.info("Retrieved {} tasks for user ID: {} sorted by priority", tasks.size(), userId);
        return ResponseEntity.ok(tasks);
    }


    @Operation(summary = "Get task statistics",
            description = "Retrieve task statistics either globally or for a specific user")
    @ApiResponse(responseCode = "200", description = "Task statistics retrieved successfully")
    @GetMapping("/stats")
    public ResponseEntity<TaskStatsResponse> getTaskStats(
            @Parameter(description = "User ID for user-specific stats (optional)") @RequestParam(required = false) Long userId) {
        log.info("Fetching task statistics for user ID: {}", userId);

        TaskStatsResponse stats = new TaskStatsResponse();

        if (userId != null) {
            stats.setTotalTasks(taskService.getTaskCountByUserId(userId));
            stats.setUserId(userId);
        } else {
            stats.setTotalTasks(taskService.getTotalTaskCount());
            // Add counts by status
            stats.setTodoCount(taskService.getTaskCountByStatus(ETaskStatus.TODO));
            stats.setInProgressCount(taskService.getTaskCountByStatus(ETaskStatus.IN_PROGRESS));
            stats.setCompletedCount(taskService.getTaskCountByStatus(ETaskStatus.COMPLETED));
        }

        log.info("Retrieved task statistics: {}", stats);
        return ResponseEntity.ok(stats);
    }


    @Operation(summary = "Delete all tasks for a user",
            description = "Remove all tasks associated with a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<TaskDeletionResponse> deleteAllTasksByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.info("Deleting all tasks for user ID: {}", userId);

        long deletedCount = taskService.deleteAllTasksByUserId(userId);

        TaskDeletionResponse response = new TaskDeletionResponse();
        response.setDeletedCount(deletedCount);
        response.setUserId(userId);
        response.setMessage("Successfully deleted " + deletedCount + " tasks for user " + userId);

        log.info("Successfully deleted {} tasks for user ID: {}", deletedCount, userId);
        return ResponseEntity.ok(response);
    }
}