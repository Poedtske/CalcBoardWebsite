package com.example.backend.enums;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.HashSet;


public enum Category {
    FOOD("FOOD"), DRINK("DRINK");

    private final String value;

    Category(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

}
