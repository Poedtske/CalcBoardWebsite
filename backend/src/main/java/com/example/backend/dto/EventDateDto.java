package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDateDto {
    private String date; // In dd/MM/yyyy format
    private String startTime; // In HH:mm format
    private String endTime; // In HH:mm format

}

