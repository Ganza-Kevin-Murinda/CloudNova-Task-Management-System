package com.taskmgt.CloudNova.repository;

import com.taskmgt.CloudNova.model.Task;
import com.taskmgt.CloudNova.model.ETaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TaskRepository {

    private final TaskStore taskStore;

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

            Long newId = taskStore.getIdGenerator().getAndIncrement();
            task.setId(newId);
            task.setCreationTimestamp();

            taskStore.getTasks().put(task.getId(), task);
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
            List<Task> allTasks = new ArrayList<>(taskStore.getTasks().values());
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
            Task removedTask = taskStore.getTasks().remove(id);
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

            Task existingTask = taskStore.getTasks().get(task.getId());
            if (existingTask == null) {
                throw new IllegalArgumentException("Task with ID " + task.getId() + " does not exist");
            }

            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setPriority(task.getPriority());
            existingTask.setStatus(task.getStatus());
            existingTask.setUserId(task.getUserId());

            existingTask.setUpdateTimestamp();

            taskStore.getTasks().put(existingTask.getId(), existingTask);

            log.info("Successfully updated task: {} with ID: {}", existingTask.getTitle(), existingTask.getId());

            return existingTask;
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
            List<Long> taskIdsToDelete = taskStore.getTasks().values().stream()
                    .filter(task -> userId.equals(task.getUserId()))
                    .map(Task::getId)
                    .toList();

            long deletedCount = 0;
            for (Long taskId : taskIdsToDelete) {
                if (taskStore.getTasks().remove(taskId) != null) {
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
            long taskCount = taskStore.getTasks().size();
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
            long userTaskCount = taskStore.getTasks().values().stream()
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
            long statusTaskCount = taskStore.getTasks().values().stream()
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

    private void validateIdExists(Long id) {
        if (id == null || !taskStore.getTasks().containsKey(id)) {
            throw new IllegalArgumentException("Task with ID " + id + " does not exist");
        }
    }

    private void validateIdNotExists(Long id) {
        if (id != null && taskStore.getTasks().containsKey(id)) {
            throw new IllegalArgumentException("Task with ID " + id + " already exists");
        }
    }

}