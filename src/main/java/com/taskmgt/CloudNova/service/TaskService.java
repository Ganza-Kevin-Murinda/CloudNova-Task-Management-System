package com.taskmgt.CloudNova.service;

import com.taskmgt.CloudNova.exception.TaskNotFoundException;
import com.taskmgt.CloudNova.exception.UserNotFoundException;
import com.taskmgt.CloudNova.model.Task;
import com.taskmgt.CloudNova.model.ETaskStatus;
import com.taskmgt.CloudNova.model.EPriority;

import java.util.List;

/**
 * Service interface for Task business operations
 * Defines the contract for task management operations
 */
public interface TaskService {

    /**
     * Create a new task with user existence validation
     * @param task the task to create
     * @return the created task with generated ID
     * @throws UserNotFoundException if the associated user doesn't exist
     * @throws IllegalArgumentException if task data is invalid
     */
    Task createTask(Task task);

    /**
     * Get a task by its ID
     * @param id the task ID
     * @return the task if found
     * @throws TaskNotFoundException if task doesn't exist
     */
    Task getTaskById(Long id);

    /**
     * Get all tasks in the system
     * @return list of all tasks
     */
    List<Task> getAllTasks();

    /**
     * Get all tasks belonging to a specific user
     * @param userId the user ID
     * @return list of user's tasks
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getTasksByUserId(Long userId);

    /**
     * Get all tasks with a specific status
     * @param status the task status
     * @return list of tasks with the specified status
     */
    List<Task> getTasksByStatus(ETaskStatus status);

    /**
     * Get all tasks with a specific priority
     * @param priority the task priority
     * @return list of tasks with the specified priority
     */
    List<Task> getTasksByPriority(EPriority priority);

    /**
     * Update an existing task
     * @param id the task ID to update
     * @param task the updated task data
     * @return the updated task
     * @throws TaskNotFoundException if task doesn't exist
     * @throws UserNotFoundException if the associated user doesn't exist
     */
    Task updateTask(Long id, Task task);

    /**
     * Update only the status of a task
     * @param id the task ID
     * @param status the new status
     * @return the updated task
     * @throws TaskNotFoundException if task doesn't exist
     */
    Task updateTaskStatus(Long id, ETaskStatus status);

    /**
     * Update only the priority of a task
     * @param id the task ID
     * @param priority the new priority
     * @return the updated task
     * @throws TaskNotFoundException if task doesn't exist
     */
    Task updateTaskPriority(Long id, EPriority priority);

    /**
     * Delete a task by ID
     * @param id the task ID to delete
     * @throws TaskNotFoundException if task doesn't exist
     */
    void deleteTask(Long id);

    /**
     * Get tasks by user ID and status (complex filtering)
     * @param userId the user ID
     * @param status the task status
     * @return list of tasks matching both criteria
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getTasksByUserAndStatus(Long userId, ETaskStatus status);

    /**
     * Get tasks by user ID and priority
     * @param userId the user ID
     * @param priority the task priority
     * @return list of tasks matching both criteria
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getTasksByUserAndPriority(Long userId, EPriority priority);

    /**
     * Get completed tasks for a specific user
     * @param userId the user ID
     * @return list of completed tasks
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getCompletedTasksByUserId(Long userId);

    /**
     * Get pending tasks for a specific user (TODO and IN_PROGRESS)
     * @param userId the user ID
     * @return list of pending tasks
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getPendingTasksByUserId(Long userId);

    /**
     * Get high priority tasks for a specific user
     * @param userId the user ID
     * @return list of high priority tasks
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getHighPriorityTasksByUserId(Long userId);

    /**
     * Search tasks by title containing specific text
     * @param titlePart the text to search for in task titles
     * @return list of tasks with matching titles
     */
    List<Task> searchTasksByTitle(String titlePart);

    /**
     * Get all tasks sorted by creation date (newest first)
     * @return list of tasks sorted by creation date
     */
    List<Task> getAllTasksSortedByCreatedDate();

    /**
     * Get user's tasks sorted by priority (HIGH -> MEDIUM -> LOW)
     * @param userId the user ID
     * @return list of user's tasks sorted by priority
     * @throws UserNotFoundException if user doesn't exist
     */
    List<Task> getTasksByUserIdSortedByPriority(Long userId);

    /**
     * Get total count of tasks
     * @return the total number of tasks
     */
    long getTotalTaskCount();

    /**
     * Get count of tasks for a specific user
     * @param userId the user ID
     * @return the number of tasks for the user
     * @throws UserNotFoundException if user doesn't exist
     */
    long getTaskCountByUserId(Long userId);

    /**
     * Get count of tasks by status
     * @param status the task status
     * @return the number of tasks with the specified status
     */
    long getTaskCountByStatus(ETaskStatus status);

    /**
     * Delete all tasks for a specific user
     * @param userId the user ID
     * @return the number of tasks deleted
     * @throws UserNotFoundException if user doesn't exist
     */
    long deleteAllTasksByUserId(Long userId);
}