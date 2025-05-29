package com.taskmgt.CloudNova.model;

import lombok.Getter;

@Getter
public enum ETaskStatus {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    ETaskStatus(String displayName) {
        this.displayName = displayName;
    }

}

