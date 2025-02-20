package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpondEventDto {
    private String spondId;
    private String startTime;
    private String endTime;
    private String title;
    private String location;
    private String description;

    // Constructor
    public SpondEventDto(String spondId, String startTime, String endTime, String title, String location, String description) {
        this.spondId = spondId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
        this.description = description;
    }

    public SpondEventDto(String spondId, String startTime, String endTime, String title, String location) {
        this.spondId = spondId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
    }

    // Getters and Setters
    public String getSpondId() {
        return spondId;
    }

    public void setSpondId(String spondId) {
        this.spondId = spondId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

