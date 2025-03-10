package com.example.backend.enums;

public enum Role {
    ADMIN("ADMIN"), USER("USER");

    private final String value;

    Role(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}

