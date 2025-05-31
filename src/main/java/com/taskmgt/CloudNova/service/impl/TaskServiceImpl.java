package com.taskmgt.CloudNova.service.impl;

import com.taskmgt.CloudNova.exception.TaskNotFoundException;
import com.taskmgt.CloudNova.exception.UserNotFoundException;
import com.taskmgt.CloudNova.model.Task;
import com.taskmgt.CloudNova.model.ETaskStatus;
import com.taskmgt.CloudNova.model.EPriority;
import com.taskmgt.CloudNova.repository.TaskRepository;
import com.taskmgt.CloudNova.repository.TaskSearchRepository;
import com.taskmgt.CloudNova.repository.UserRepository;
import com.taskmgt.CloudNova.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of TaskService interface
 * Provides business logic for task management operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskSearchRepository taskSearchRepository;
    private final UserRepository userRepository;

    @Override
    public Task createTask(Task task) {
        log.info("Creating new task: {}", task.getTitle());

        // Validate input
        validateTaskInput(task);

        // Validate user exists
        validateUserExists(task.getUserId());

        // Set default status if not provided
        if (task.getStatus() == null) {
            task.setStatus(ETaskStatus.TODO);
            log.debug("Setting default status to TODO for task: {}", task.getTitle());
        }

        // Set default priority if not provided
        if (task.getPriority() == null) {
            task.setPriority(EPriority.MEDIUM);
            log.debug("Setting default priority to MEDIUM for task: {}", task.getTitle());
        }

        try {
            Task createdTask = taskRepository.createTask(task);
            log.info("Successfully created task: {} with ID: {}", createdTask.getTitle(), createdTask.getId());
            return createdTask;
        } catch (Exception e) {
            log.error("Failed to create task: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create task: " + e.getMessage(), e);
        }
    }

    @Override
    public Task getTaskById(Long id) {
        log.debug("Retrieving task by ID: {}", id);

        validateTaskId(id);

        return taskSearchRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task not found with ID: {}", id);
                    return new TaskNotFoundException(id);
                });
    }

    @Override
    public List<Task> getAllTasks() {
        log.debug("Retrieving all tasks");

        try {
            List<Task> tasks = taskRepository.findAll();
            log.debug("Retrieved {} tasks", tasks.size());
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve all tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByUserId(Long userId) {
        log.debug("Retrieving tasks for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            List<Task> tasks = taskSearchRepository.findByUserId(userId);
            log.debug("Retrieved {} Tasks for user ID: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByStatus(ETaskStatus status) {
        log.debug("Retrieving tasks by status: {}", status);

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        try {
            List<Task> tasks = taskSearchRepository.findByStatus(status);
            log.debug("Retrieved {} tasks with status: {}", tasks.size(), status);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks by status {}: {}", status, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve tasks by status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByPriority(EPriority priority) {
        log.debug("Retrieving tasks by priority: {}", priority);

        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }

        try {
            List<Task> tasks = taskSearchRepository.findByPriority(priority);
            log.debug("Retrieved {} tasks with priority: {}", tasks.size(), priority);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks by priority {}: {}", priority, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve tasks by priority: " + e.getMessage(), e);
        }
    }

    @Override
    public Task updateTask(Long id, Task task) {
        log.info("Updating task with ID: {}", id);

        validateTaskId(id);
        validateTaskInput(task);

        // Verify task exists
        Task existingTask = getTaskById(id);

        // Validate user exists if user ID is being changed
        if (!existingTask.getUserId().equals(task.getUserId())) {
            validateUserExists(task.getUserId());
        }

        // Set the ID to ensure we're updating the correct task
        task.setId(id);

        try {
            Task updatedTask = taskRepository.updateTask(task);
            log.info("Successfully updated task: {} with ID: {}", updatedTask.getTitle(), id);
            return updatedTask;
        } catch (Exception e) {
            log.error("Failed to update task with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        }
    }

    @Override
    public Task updateTaskStatus(Long id, ETaskStatus status) {
        log.info("Updating task status for ID: {} to {}", id, status);

        validateTaskId(id);
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        Task existingTask = getTaskById(id);
        existingTask.setStatus(status);

        try {
            Task updatedTask = taskRepository.updateTask(existingTask);
            log.info("Successfully updated task status for ID: {} to {}", id, status);
            return updatedTask;
        } catch (Exception e) {
            log.error("Failed to update task status for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update task status: " + e.getMessage(), e);
        }
    }

    @Override
    public Task updateTaskPriority(Long id, EPriority priority) {
        log.info("Updating task priority for ID: {} to {}", id, priority);

        validateTaskId(id);
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }

        Task existingTask = getTaskById(id);
        existingTask.setPriority(priority);

        try {
            Task updatedTask = taskRepository.updateTask(existingTask);
            log.info("Successfully updated task priority for ID: {} to {}", id, priority);
            return updatedTask;
        } catch (Exception e) {
            log.error("Failed to update task priority for ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to update task priority: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteTask(Long id) {
        log.info("Deleting task with ID: {}", id);

        validateTaskId(id);

        // Verify task exists before attempting to delete
        getTaskById(id);

        try {
            boolean deleted = taskRepository.deleteById(id);
            if (deleted) {
                log.info("Successfully deleted task with ID: {}", id);
            } else {
                log.warn("Task with ID {} was not found for deletion", id);
                throw new TaskNotFoundException(id);
            }
        } catch (Exception e) {
            log.error("Failed to delete task with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByUserAndStatus(Long userId, ETaskStatus status) {
        log.debug("Retrieving tasks for user ID: {} with status: {}", userId, status);

        validateUserId(userId);
        validateUserExists(userId);
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        try {
            List<Task> tasks = taskSearchRepository.findByUserIdAndStatus(userId, status);
            log.debug("Retrieved {} tasks for user ID: {} with status: {}", tasks.size(), userId, status);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks for user ID {} and status {}: {}", userId, status, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve filtered tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByUserAndPriority(Long userId, EPriority priority) {
        log.debug("Retrieving tasks for user ID: {} with priority: {}", userId, priority);

        validateUserId(userId);
        validateUserExists(userId);
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }

        try {
            List<Task> tasks = taskSearchRepository.findByUserIdAndPriority(userId, priority);
            log.debug("Retrieved {} tasks for user ID: {} with priority: {}", tasks.size(), userId, priority);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks for user ID {} and priority {}: {}", userId, priority, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve filtered tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getCompletedTasksByUserId(Long userId) {
        log.debug("Retrieving completed tasks for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            List<Task> tasks = taskSearchRepository.findCompletedTasksByUserId(userId);
            log.debug("Retrieved {} completed tasks for user ID: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve completed tasks for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve completed tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getPendingTasksByUserId(Long userId) {
        log.debug("Retrieving pending tasks for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            List<Task> tasks = taskSearchRepository.findPendingTasksByUserId(userId);
            log.debug("Retrieved {} pending tasks for user ID: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve pending tasks for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve pending tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getHighPriorityTasksByUserId(Long userId) {
        log.debug("Retrieving high priority tasks for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            List<Task> tasks = taskSearchRepository.findHighPriorityTasksByUserId(userId);
            log.debug("Retrieved {} high priority tasks for user ID: {}", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve high priority tasks for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve high priority tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> searchTasksByTitle(String titlePart) {
        log.debug("Searching tasks by title containing: {}", titlePart);

        if (titlePart == null || titlePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Title search text cannot be null or empty");
        }

        try {
            List<Task> tasks = taskSearchRepository.findByTitleContaining(titlePart);
            log.debug("Found {} tasks with title containing: {}", tasks.size(), titlePart);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to search tasks by title {}: {}", titlePart, e.getMessage(), e);
            throw new RuntimeException("Failed to search tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getAllTasksSortedByCreatedDate() {
        log.debug("Retrieving all tasks sorted by creation date");

        try {
            List<Task> tasks = taskSearchRepository.findAllSortedByCreatedDate();
            log.debug("Retrieved {} tasks sorted by creation date", tasks.size());
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks sorted by creation date: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve sorted tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> getTasksByUserIdSortedByPriority(Long userId) {
        log.debug("Retrieving tasks for user ID: {} sorted by priority", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            List<Task> tasks = taskSearchRepository.findByUserIdSortedByPriority(userId);
            log.debug("Retrieved {} tasks for user ID: {} sorted by priority", tasks.size(), userId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to retrieve tasks for user ID {} sorted by priority: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve sorted tasks: " + e.getMessage(), e);
        }
    }

    @Override
    public long getTotalTaskCount() {
        log.debug("Getting total task count");

        try {
            long count = taskRepository.count();
            log.debug("Total task count: {}", count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get total task count: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get task count: " + e.getMessage(), e);
        }
    }

    @Override
    public long getTaskCountByUserId(Long userId) {
        log.debug("Getting task count for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            long count = taskRepository.countByUserId(userId);
            log.debug("Task count for user ID {}: {}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get task count for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to get user task count: " + e.getMessage(), e);
        }
    }

    @Override
    public long getTaskCountByStatus(ETaskStatus status) {
        log.debug("Getting task count by status: {}", status);

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        try {
            long count = taskRepository.countByStatus(status);
            log.debug("Task count for status {}: {}", status, count);
            return count;
        } catch (Exception e) {
            log.error("Failed to get task count by status {}: {}", status, e.getMessage(), e);
            throw new RuntimeException("Failed to get status task count: " + e.getMessage(), e);
        }
    }

    @Override
    public long deleteAllTasksByUserId(Long userId) {
        log.info("Deleting all tasks for user ID: {}", userId);

        validateUserId(userId);
        validateUserExists(userId);

        try {
            long deletedCount = taskRepository.deleteByUserId(userId);
            log.info("Successfully deleted {} tasks for user ID: {}", deletedCount, userId);
            return deletedCount;
        } catch (Exception e) {
            log.error("Failed to delete tasks for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete user tasks: " + e.getMessage(), e);
        }
    }

    // ─── Private Validation Methods ─────────────────────────────────

    /**
     * Validate task input data
     */
    private void validateTaskInput(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }

        if (task.getTitle().length() > 100) {
            throw new IllegalArgumentException("Task title must not exceed 100 characters");
        }

        if (task.getDescription() == null || task.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be null or empty");
        }

        if (task.getDescription().length() > 500) {
            throw new IllegalArgumentException("Task description must not exceed 500 characters");
        }

        if (task.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
    }

    /**
     * Validate task ID
     */
    private void validateTaskId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Task ID must be a positive number");
        }
    }

    /**
     * Validate user ID
     */
    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }

    /**
     * Validate that user exists
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User not found with ID: {}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
}