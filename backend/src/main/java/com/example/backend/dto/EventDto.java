package com.example.backend.dto;

import com.example.backend.enums.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Builder
@Data
public class EventDto {
    private Long id;
    private String title;
    private String location;
    private EventType type;
    private String description;
    private String startTime;
    private String endTime;
    private int seatsPerTable;
    private String img;
    private String layout;
    private BigDecimal ticketPrice;
    private int rijen;
    private int kolommen;


    public void CreateLayout(){
        if (LayoutFormatter()) {
            // Height and length successfully added
            LayoutFormatter();
            System.out.println("created layout");
        } else {
            System.out.println("Failed to create layout");
        }
    }

    private Boolean LayoutFormatter() {
        if (layout == null || layout.isEmpty()) {
            System.out.println("Layout is null or empty.");
            return false;
        }
        layout = layout.trim().toLowerCase();
        String[] array = layout.split("x");
        if (array.length != 2) {
            System.out.println("Layout format is invalid. Expected format: 'AxB', got: " + layout);
            return false;
        }
        try {
            this.kolommen = Integer.parseInt(array[0]);
            this.rijen = Integer.parseInt(array[1]);
            return true; // Successfully parsed
        } catch (NumberFormatException e) {
            System.out.println("Error parsing layout: " + e.getMessage());
            return false; // Parsing failed
        }
    }
}
