package com.example.backend.controller;

import com.example.backend.dto.SpondEventDto;
import com.example.backend.model.Event;
import com.example.backend.service.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling Spond event-related operations.
 * <p><strong>This controller is outdated and belongs to the
 * main branch, it's still usefull for seeding the database but
 * it's handling are outdated</strong></p>
 * <p>
 * This controller provides endpoints for adding, updating, deleting, and retrieving Spond events.
 * It interacts with the {@link EventServiceImpl} to manage the event lifecycle.
 * </p>
 *
 * <p>Base URL: Various endpoints under <strong>/api/secure/spondEvents</strong> and <strong>/api/public/spondEvents</strong></p>
 */
@RestController
@RequestMapping
public class SpondEventAPIController {

    @Autowired
    private EventServiceImpl service;

    /**
     * Adds a new Spond event.
     * <p>
     * Checks if an event with the same Spond ID already exists before saving the new event.
     * </p>
     *
     * @param e {@link Event} The event to be added.
     * @return {@link String} A message indicating whether the event was added or already exists.
     */
    @PostMapping("/api/secure/spondEvents")
    public String addSpondEvent(@RequestBody Event e) {
        if (service.findById(e.getSpondId()) != null) {
            return "Event with spondId " + e.getSpondId() + " already exists";
        } else {
            service.save(e);
            return "Event has been added";
        }
    }

    /**
     * Adds multiple Spond events in a batch operation.
     * <p>
     * Processes a list of events and adds them if they do not already exist.
     * </p>
     *
     * @param events {@link List<Event>} List of events to be added.
     * @return {@link String} A message indicating whether all events were successfully added.
     */
    @PostMapping("/api/secure/spondEvents/batch")
    public String addMultipleEvents(@RequestBody List<Event> events) {
        for (Event event : events) {
            if (service.findById(event.getSpondId()) != null) {
                return "Event with spondId " + event.getSpondId() + " already exists";
            } else {
                service.save(event);
            }
        }
        return "All events have been added successfully";
    }

    /**
     * Deletes a specific Spond event.
     * <p>
     * Deletes the event identified by its Spond ID.
     * </p>
     *
     * @param e {@link Event} The event to be deleted.
     * @return {@link String} A confirmation message that the event was deleted.
     */
    @DeleteMapping("/api/secure/spondEvents")
    public String DeleteSpondEvent(@RequestBody Event e) {
        service.deleteEvent(service.findById(e.getSpondId()));
        return "Spond event has been deleted";
    }

    /**
     * Updates an existing Spond event.
     * <p>
     * Replaces the details of an existing event with new details provided in the request body.
     * </p>
     *
     * @param e {@link Event} The updated event data.
     * @return {@link String} A confirmation message that the event was updated.
     */
    @PutMapping("/api/secure/spondEvents")
    public String UpdateSpondEvent(@RequestBody Event e) {
        Event oldEvent = service.findById(e.getSpondId());
        oldEvent.SpondUpdate(e);
        service.save(oldEvent);
        return "Spond event has been updated";
    }

    /**
     * Retrieves all Spond events.
     * <p>
     * Fetches and converts all events with a non-null Spond ID into {@link SpondEventDto} objects.
     * </p>
     *
     * @return {@link Iterable<SpondEventDto>} List of Spond events in DTO format.
     */
    @GetMapping("/api/public/spondEvents")
    public Iterable<SpondEventDto> getAllSpondEvents() {
        Iterable<Event> allEvents = service.findAll();
        List<SpondEventDto> eventDtos = new ArrayList<>();

        for (Event event : allEvents) {
            if (event.getSpondId() != null) { // Filter events with valid Spond IDs
                SpondEventDto eventDto;

                // Handle optional description field
                if (event.getDescription() == null) {
                    eventDto = new SpondEventDto(
                            event.getSpondId(),
                            event.getStartTime().toString(),
                            event.getEndTime().toString(),
                            event.getTitle(),
                            event.getLocation()
                    );
                } else {
                    eventDto = new SpondEventDto(
                            event.getSpondId(),
                            event.getStartTime().toString(),
                            event.getEndTime().toString(),
                            event.getTitle(),
                            event.getLocation(),
                            event.getDescription()
                    );
                }

                eventDtos.add(eventDto);
            }
        }
        return eventDtos;
    }
}

