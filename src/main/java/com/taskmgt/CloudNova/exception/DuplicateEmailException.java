package com.taskmgt.CloudNova.exception;

/**
 * Exception thrown when attempting to create or update a user with an email that already exists
 */
public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String email) {
    super("User with email '" + email + "' already exists");
  }

  public DuplicateEmailException(String message, Throwable cause) {
    super(message, cause);
  }
}