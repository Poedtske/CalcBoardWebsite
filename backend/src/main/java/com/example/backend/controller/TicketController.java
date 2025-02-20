package com.example.backend.controller;

import com.example.backend.dto.TicketDto;
import com.example.backend.service.impl.TicketServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *     RestController that manages the endpoints related to tickets
 * </p>
 */
@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketServiceImpl ticketService;

    /**
     * Endpoint for retrieving all tickets for a specific event.
     * <p><strong>Currently not being used by the frontend</strong></p>
     * <p>
     * This method retrieves all tickets associated with a specific event, based on the event ID provided in the URL path.
     * It is intended for admin use, as it provides access to all tickets for any event.
     * </p>
     *
     *
     * @param eventId The ID of the event for which tickets are being requested.
     *                This ID is provided as a path variable in the URL.
     * @return A {@link ResponseEntity} containing a list of {@link TicketDto} objects, representing all tickets for the specified event.
     *         If successful, it returns the list of tickets as the response body.
     */
    @GetMapping("/api/admin/events/{eventId}/tickets")
    public ResponseEntity<Iterable<TicketDto>> getTickets(@PathVariable("eventId") Long eventId){
        return ResponseEntity.ok(ticketService.findAll());
    }

    /**
     * Endpoint for retrieving a specific ticket by its ID.
     * <p><strong>Currently not being used by the frontend</strong></p>
     * <p>
     * This method retrieves the details of a single ticket based on its ID.
     * It is an admin-only endpoint, allowing retrieval of individual ticket information.
     * </p>
     *
     * @param id The ID of the ticket to be retrieved. This ID is provided as a path variable in the URL.
     * @return A {@link ResponseEntity} containing a {@link TicketDto} representing the ticket with the specified ID.
     *         If successful, the ticket's details are returned as the response body.
     */
    @GetMapping("/api/admin/tickets/{id}")
    public ResponseEntity getTicketById(@PathVariable("id") Long id){
        return ResponseEntity.ok(ticketService.findTicketDtoById(id));
    }

    /**
     * Endpoint for creating a new ticket for the authenticated user.
     * <p>
     * This method allows an authenticated user to create a new ticket.
     * The ticket data is passed as a {@link TicketDto} in the request body.
     * The user's identity is retrieved from the security context to associate the ticket with the correct user.
     * </p>
     *
     * @param ticketDto A {@link TicketDto} object containing the details of the ticket to be created.
     * @return A {@link ResponseEntity} representing the result of the ticket creation.
     *         If the ticket is successfully created, it returns the response body with relevant details.
     */
    @PostMapping("/api/secure/tickets")
    public ResponseEntity createTicket(@RequestBody TicketDto ticketDto ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Get the logged-in username
        return ticketService.createTicket(username,ticketDto);
    }

}

