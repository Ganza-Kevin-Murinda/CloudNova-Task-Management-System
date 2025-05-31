package com.taskmgt.CloudNova.repository;

import com.taskmgt.CloudNova.model.Task;
import com.taskmgt.CloudNova.model.ETaskStatus;
import com.taskmgt.CloudNova.model.EPriority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class TaskRepository {

    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    // ─── Core CRUD ───────────────────────────────────────────────────

    /**
     * Save a task (CREATE or UPDATE operation)
     * @param task the task to save
     * @return the saved task with generated ID if new
     */
    public Task createTask(Task task) {
        log.debug("Attempting to create task: {}", task.getTitle());

        try {
            validateIdNotExists(task.getId());

            Long newId = idGenerator.getAndIncrement();
            task.setId(newId);
            task.setCreationTimestamp();

            tasks.put(task.getId(), task);
            log.info("Successfully created task: {} with ID: {}", task.getTitle(), task.getId());

            return task;
        } catch (Exception e) {
            log.error("Error creating task: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Find all tasks
     * @return List of all tasks
     */
    public List<Task> findAll() {
        log.debug("Finding all tasks");

        try {
            List<Task> allTasks = new ArrayList<>(tasks.values());
            log.debug("Found {} tasks", allTasks.size());
            return allTasks;
        } catch (Exception e) {
            log.error("Error finding all tasks: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Delete task by ID
     * @param id the task ID to delete
     * @return true if a task was deleted, false if not found
     */
    public boolean deleteById(Long id) {
        log.debug("Attempting to delete task by ID: {}", id);

        validateIdExists(id);

        try {
            Task removedTask = tasks.remove(id);
            boolean deleted = removedTask != null;

            if (deleted) {
                log.info("Successfully deleted task: {} with ID: {}", removedTask.getTitle(), id);
            } else {
                log.debug("No task found to delete with ID: {}", id);
            }

            return deleted;
        } catch (Exception e) {
            log.error("Error deleting task by ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Save a task (CREATE or UPDATE operation)
     * @param task the task to save
     * @return the saved task with generated ID if new
     */
    public Task updateTask(Task task) {
        log.debug("Attempting to update task: {}", task.getTitle());

        try {
            validateIdExists(task.getId());

            task.setUpdateTimestamp();
            tasks.put(task.getId(), task);

            log.info("Successfully updated task: {} with ID: {}", task.getTitle(), task.getId());

            return task;
        } catch (Exception e) {
            log.error("Error updating task: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete all tasks by user ID
     * @param userId the user ID
     * @return number of tasks deleted
     */
    public long deleteByUserId(Long userId) {
        log.debug("Attempting to delete all tasks by user ID: {}", userId);

        if (userId == null) {
            log.warn("Cannot delete tasks with null user ID");
            return 0;
        }

        try {
            List<Long> taskIdsToDelete = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()))
                    .map(Task::getId)
                    .toList();

            long deletedCount = 0;
            for (Long taskId : taskIdsToDelete) {
                if (tasks.remove(taskId) != null) {
                    deletedCount++;
                }
            }

            log.info("Successfully deleted {} tasks for user ID: {}", deletedCount, userId);
            return deletedCount;
        } catch (Exception e) {
            log.error("Error deleting tasks by user ID {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get total count of tasks
     * @return the number of tasks
     */
    public long count() {
        log.debug("Getting task count");

        try {
            long taskCount = tasks.size();
            log.debug("Total tasks count: {}", taskCount);
            return taskCount;
        } catch (Exception e) {
            log.error("Error getting task count: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get count of tasks by user ID
     * @param userId the user ID
     * @return the number of tasks for the user
     */
    public long countByUserId(Long userId) {
        log.debug("Getting task count by user ID: {}", userId);

        if (userId == null) {
            log.warn("Cannot count tasks with null user ID");
            return 0;
        }

        try {
            long userTaskCount = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()))
                    .count();
            log.debug("Task count for user ID {}: {}", userId, userTaskCount);
            return userTaskCount;
        } catch (Exception e) {
            log.error("Error getting task count by user ID {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Get count of tasks by status
     * @param status the task status
     * @return the number of tasks with the specified status
     */
    public long countByStatus(ETaskStatus status) {
        log.debug("Getting task count by status: {}", status);

        if (status == null) {
            log.warn("Cannot count tasks with null status");
            return 0;
        }

        try {
            long statusTaskCount = tasks.values().stream()
                    .filter(task -> status.equals(task.getStatus()))
                    .count();
            log.debug("Task count for status {}: {}", status, statusTaskCount);
            return statusTaskCount;
        } catch (Exception e) {
            log.error("Error getting task count by status {}: {}", status, e.getMessage(), e);
            return 0;
        }
    }



    // ─── Validations ─────────────────────────────────────────────────

    /**
     * Check if a task exists by ID
     * @param id the task ID to check
     * @return true if a task exists, false otherwise
     */
    public boolean existsById(Long id) {
        log.debug("Checking if task exists by ID: {}", id);

        if (id == null) {
            log.warn("Cannot check existence for null ID");
            return false;
        }

        try {
            boolean exists = tasks.containsKey(id);
            log.debug("Task with ID {} exists: {}", id, exists);
            return exists;
        } catch (Exception e) {
            log.error("Error checking task existence by ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    private void validateIdExists(Long id) {
        if (id == null || !tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with ID " + id + " does not exist");
        }
    }

    private void validateIdNotExists(Long id) {
        if (id != null && tasks.containsKey(id)) {
            throw new IllegalArgumentException("Task with " + id + " already exists");
        }
    }

    /**
     * Clear all tasks (for testing purposes)
     */
    public void deleteAll() {
        log.warn("Deleting all tasks from repository");
        try {
            int count = tasks.size();
            tasks.clear();
            idGenerator.set(1);
            log.info("Successfully deleted {} tasks", count);
        } catch (Exception e) {
            log.error("Error deleting all tasks: {}", e.getMessage(), e);
        }
    }

}