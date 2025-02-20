package com.example.backend.storage;


import org.springframework.context.annotation.Configuration;

@Configuration(value = "storage")
public class StorageProperties {
    private String location = "images";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;

    }
}
