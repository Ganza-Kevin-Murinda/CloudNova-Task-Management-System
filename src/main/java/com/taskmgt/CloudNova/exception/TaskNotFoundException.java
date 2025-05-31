package com.taskmgt.CloudNova.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
    public TaskNotFoundException(Long id) {
        super("Task not found with id: " + id);
    }

    public TaskNotFoundException(String field, String value) {
        super("Task not found with " + field + ": " + value);
    }
}
