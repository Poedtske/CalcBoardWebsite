package com.example.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TableForm {
    private int width;
    private int height;
    private int seats;
    private Long eventId; // ID of the selected event
    private int aantalTafels; // Number of tables to create

    // Getters and setters
}

