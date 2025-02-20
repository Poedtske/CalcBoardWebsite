package com.example.backend.service;

import com.example.backend.dto.EventDto;
import com.example.backend.model.Event;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface EventService {
    Event save(Event event);

    ResponseEntity updateById(Long id, EventDto eventDto);

    Iterable<Event> findAll();

    Event findById(Long id);

    Event findById(String id);

    ResponseEntity deleteById(Long id) throws IOException;

    ResponseEntity deleteById(String id) throws IOException;

    void deleteEvent(Event e);
}
