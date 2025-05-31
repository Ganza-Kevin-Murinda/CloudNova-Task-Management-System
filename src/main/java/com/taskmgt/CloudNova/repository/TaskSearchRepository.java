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
public class TaskSearchRepository {

    private final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // ─── Search & Queries ────────────────────────────────────────────

    /**
     * Find task by ID
     * @param id the task ID
     * @return Optional containing the task if found
     */
    public Optional<Task> findById(Long id) {
        log.debug("Finding task by ID: {}", id);

        if (id == null) {
            log.warn("Cannot find task with null ID");
            return Optional.empty();
        }

        try {
            Task task = tasks.get(id);
            if (task != null) {
                log.debug("Found task: {} with ID: {}", task.getTitle(), id);
                return Optional.of(task);
            } else {
                log.debug("No task found with ID: {}", id);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error finding task by ID {}: {}", id, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Find tasks by user ID
     * @param userId the user ID
     * @return List of tasks belonging to the user
     */
    public List<Task> findByUserId(Long userId) {
        log.debug("Finding tasks by user ID: {}", userId);

        if (userId == null) {
            log.warn("Cannot find Tasks with null user ID");
            return new ArrayList<>();
        }

        try {
            List<Task> userTasks = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()))
                    .collect(Collectors.toList());

            log.debug("Found {} Tasks for user ID: {}", userTasks.size(), userId);
            return userTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by user ID {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find tasks by status
     * @param status the task status
     * @return List of tasks with the specified status
     */
    public List<Task> findByStatus(ETaskStatus status) {
        log.debug("Finding tasks by status: {}", status);

        if (status == null) {
            log.warn("Cannot find tasks with null status");
            return new ArrayList<>();
        }

        try {
            List<Task> statusTasks = tasks.values().stream()
                    .filter(task -> status.equals(task.getStatus()))
                    .collect(Collectors.toList());

            log.debug("Found {} tasks with status: {}", statusTasks.size(), status);
            return statusTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by status {}: {}", status, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find tasks by priority
     * @param priority the task priority
     * @return List of tasks with the specified priority
     */
    public List<Task> findByPriority(EPriority priority) {
        log.debug("Finding tasks by priority: {}", priority);

        if (priority == null) {
            log.warn("Cannot find tasks with null priority");
            return new ArrayList<>();
        }

        try {
            List<Task> priorityTasks = tasks.values().stream()
                    .filter(task -> priority.equals(task.getPriority()))
                    .collect(Collectors.toList());

            log.debug("Found {} tasks with priority: {}", priorityTasks.size(), priority);
            return priorityTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by priority {}: {}", priority, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ─── Filter & Sort ────────────────────────────────────────────

    /**
     * Find tasks by user ID and status (complex filtering)
     * @param userId the user ID
     * @param status the task status
     * @return List of tasks matching both criteria
     */
    public List<Task> findByUserIdAndStatus(Long userId, ETaskStatus status) {
        log.debug("Finding tasks by user ID: {} and status: {}", userId, status);

        if (userId == null || status == null) {
            log.warn("Cannot find tasks with null user ID or status");
            return new ArrayList<>();
        }

        try {
            List<Task> filteredTasks = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()) && status.equals(task.getStatus()))
                    .collect(Collectors.toList());

            log.debug("Found {} tasks for user ID: {} with status: {}", filteredTasks.size(), userId, status);
            return filteredTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by user ID {} and status {}: {}", userId, status, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find tasks by user ID and priority
     * @param userId the user ID
     * @param priority the task priority
     * @return List of tasks matching both criteria
     */
    public List<Task> findByUserIdAndPriority(Long userId, EPriority priority) {
        log.debug("Finding tasks by user ID: {} and priority: {}", userId, priority);

        if (userId == null || priority == null) {
            log.warn("Cannot find tasks with null user ID or priority");
            return new ArrayList<>();
        }

        try {
            List<Task> filteredTasks = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()) && priority.equals(task.getPriority()))
                    .collect(Collectors.toList());

            log.debug("Found {} tasks for user ID: {} with priority: {}", filteredTasks.size(), userId, priority);
            return filteredTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by user ID {} and priority {}: {}", userId, priority, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find tasks by title containing text (case-insensitive)
     * @param titlePart the text to search for in task titles
     * @return List of tasks with titles containing the specified text
     */
    public List<Task> findByTitleContaining(String titlePart) {
        log.debug("Finding tasks by title containing: {}", titlePart);

        if (titlePart == null || titlePart.trim().isEmpty()) {
            log.warn("Cannot search for tasks with null or empty title part");
            return new ArrayList<>();
        }

        try {
            List<Task> matchingTasks = tasks.values().stream()
                    .filter(task -> task.getTitle() != null &&
                            task.getTitle().toLowerCase().contains(titlePart.toLowerCase()))
                    .collect(Collectors.toList());

            log.debug("Found {} tasks with title containing: {}", matchingTasks.size(), titlePart);
            return matchingTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by title containing {}: {}", titlePart, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find completed tasks by user ID
     * @param userId the user ID
     * @return List of completed tasks for the user
     */
    public List<Task> findCompletedTasksByUserId(Long userId) {
        log.debug("Finding completed tasks by user ID: {}", userId);
        return findByUserIdAndStatus(userId, ETaskStatus.COMPLETED);
    }

    /**
     * Find pending tasks by user ID (TODO and IN_PROGRESS)
     * @param userId the user ID
     * @return List of pending tasks for the user
     */
    public List<Task> findPendingTasksByUserId(Long userId) {
        log.debug("Finding pending tasks by user ID: {}", userId);

        if (userId == null) {
            log.warn("Cannot find pending tasks with null user ID");
            return new ArrayList<>();
        }

        try {
            List<Task> pendingTasks = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()) &&
                            (ETaskStatus.TODO.equals(task.getStatus()) ||
                                    ETaskStatus.IN_PROGRESS.equals(task.getStatus())))
                    .collect(Collectors.toList());

            log.debug("Found {} pending tasks for user ID: {}", pendingTasks.size(), userId);
            return pendingTasks;
        } catch (Exception e) {
            log.error("Error finding pending tasks by user ID {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Find high priority tasks by user ID
     * @param userId the user ID
     * @return List of high priority tasks for the user
     */
    public List<Task> findHighPriorityTasksByUserId(Long userId) {
        log.debug("Finding high priority tasks by user ID: {}", userId);
        return findByUserIdAndPriority(userId, EPriority.HIGH);
    }

    /**
     * Get tasks sorted by creation date (newest first)
     * @return List of tasks sorted by creation date
     */
    public List<Task> findAllSortedByCreatedDate() {
        log.debug("Finding all tasks sorted by creation date");

        try {
            List<Task> sortedTasks = tasks.values().stream()
                    .sorted((t1, t2) -> {
                        if (t1.getCreatedDate() == null && t2.getCreatedDate() == null) return 0;
                        if (t1.getCreatedDate() == null) return 1;
                        if (t2.getCreatedDate() == null) return -1;
                        return t2.getCreatedDate().compareTo(t1.getCreatedDate()); // Newest first
                    })
                    .collect(Collectors.toList());

            log.debug("Found {} tasks sorted by creation date", sortedTasks.size());
            return sortedTasks;
        } catch (Exception e) {
            log.error("Error finding tasks sorted by creation date: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Get tasks for user sorted by priority (HIGH -> MEDIUM -> LOW)
     * @param userId the user ID
     * @return List of user's tasks sorted by priority
     */
    public List<Task> findByUserIdSortedByPriority(Long userId) {
        log.debug("Finding tasks by user ID: {} sorted by priority", userId);

        if (userId == null) {
            log.warn("Cannot find tasks with null user ID");
            return new ArrayList<>();
        }

        try {
            List<Task> sortedTasks = tasks.values().stream()
                    .filter(task -> userId.equals(task.getUserId()))
                    .sorted((t1, t2) -> {
                        // Define priority order: HIGH = 3, MEDIUM = 2, LOW = 1
                        int p1 = getPriorityOrder(t1.getPriority());
                        int p2 = getPriorityOrder(t2.getPriority());
                        return Integer.compare(p2, p1); // Higher priority first
                    })
                    .collect(Collectors.toList());

            log.debug("Found {} tasks for user ID: {} sorted by priority", sortedTasks.size(), userId);
            return sortedTasks;
        } catch (Exception e) {
            log.error("Error finding tasks by user ID {} sorted by priority: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Helper method to get priority order for sorting
     * @param priority the priority enum
     * @return integer value for sorting (higher = more important)
     */
    private int getPriorityOrder(EPriority priority) {
        if (priority == null) return 0;
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

}
