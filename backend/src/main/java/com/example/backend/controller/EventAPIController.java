package com.example.backend.controller;

import com.example.backend.dto.EventDto;
import com.example.backend.enums.EventType;
import com.example.backend.model.Event;
import com.example.backend.service.impl.EventServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Controller responsible for managing events in the system.
 * <p>
 * This controller provides endpoints to retrieve, create, update, and delete events,
 * as well as to fetch event types and event images.
 * </p>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventAPIController {

    private final EventServiceImpl eventService;

    /**
     * Retrieves all events available to the public.
     * <p>
     * This route returns a list of all events, allowing public users to view event details.
     * </p>
     *
     * @return {@link Iterable<Event>} containing all events.
     */
    @GetMapping("/public/events")
    public Iterable<Event> getAllEvents() {
        return eventService.findAll();
    }

    /**
     * Retrieves a specific event based on the given event ID.
     * <p>
     * This route allows public users to view detailed information about a single event.
     * </p>
     *
     * @param id {@link Long} representing the unique ID of the event.
     * @return {@link Event} containing the details of the specified event.
     */
    @GetMapping("/public/events/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.findById(id);
    }

    /**
     * Creates a new event.
     * <p>
     * This route allows an admin user to create a new event by providing event details
     * and an image file for the event.
     * </p>
     *
     * @param eventDto {@link EventDto} containing the event details.
     * @param imageFile {@link MultipartFile} representing the event image.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @PostMapping(value = "/admin/events", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity createEvent(@RequestPart EventDto eventDto, @RequestPart MultipartFile imageFile) {
        return eventService.createEvent(eventDto, imageFile);
    }

    /**
     * Updates the event details, including the event image, for the specified event ID.
     * <p>
     * This route allows an admin to update the event's details and its image file.
     * </p>
     *
     * @param id {@link Long} representing the unique ID of the event to be updated.
     * @param eventDto {@link EventDto} containing the updated event details.
     * @param imageFile {@link MultipartFile} representing the updated event image.
     * @return {@link ResponseEntity} indicating the result of the operation.
     * @throws IOException if an error occurs while handling the image file.
     */
    @PutMapping("/admin/events/{id}/img")
    public ResponseEntity updateEvent(@PathVariable Long id, @RequestPart EventDto eventDto, @RequestPart MultipartFile imageFile) throws IOException {
        return eventService.updateById(id, eventDto, imageFile);
    }

    /**
     * Updates the event details without modifying the event image.
     * <p>
     * This route allows an admin to update the event's details without changing its image.
     * </p>
     *
     * @param id {@link Long} representing the unique ID of the event to be updated.
     * @param eventDto {@link EventDto} containing the updated event details.
     * @return {@link ResponseEntity} indicating the result of the operation.
     */
    @PutMapping("/admin/events/{id}")
    public ResponseEntity updateEvent(@PathVariable Long id, @RequestPart EventDto eventDto) {
        return eventService.updateById(id, eventDto);
    }

    /**
     * Retrieves a list of all possible event types.
     * <p>
     * This route returns a list of all available event types, useful for categorizing or filtering events.
     * </p>
     *
     * @return {@link List<String>} containing all event types.
     */
    @GetMapping("/public/eventTypes")
    public List<String> getEventTypes() {
        return Arrays.stream(EventType.values())
                .map(Enum::name)
                .toList();
    }

    /**
     * Deletes a specific event by its ID.
     * <p>
     * This route allows an admin to delete an event, making it no longer available to users.
     * </p>
     *
     * @param id {@link Long} representing the unique ID of the event to be deleted.
     * @return {@link ResponseEntity} indicating the result of the deletion operation.
     */
    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e);
        }
    }

    /**
     * Retrieves the image associated with the specified event ID.
     * <p>
     * This route allows users to fetch the image file of a specific event.
     * </p>
     *
     * @param id {@link Long} representing the unique ID of the event.
     * @return {@link ResponseEntity} containing the event image file.
     */
    @GetMapping("/public/events/{id}/image")
    public ResponseEntity<?> getImageByEventId(@PathVariable long id) {
        return eventService.getImg(id);
    }
}
