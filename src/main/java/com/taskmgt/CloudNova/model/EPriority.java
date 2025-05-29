package com.taskmgt.CloudNova.model;

import lombok.Getter;

@Getter
public enum EPriority {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High");

    private final String displayName;

    EPriority(String displayName) {
        this.displayName = displayName;
    }

}
