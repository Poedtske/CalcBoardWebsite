package com.example.backend.service.impl;

import com.example.backend.dto.TicketDto;
import com.example.backend.exceptions.AppException;
import com.example.backend.model.*;
import com.example.backend.repository.TicketRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    private final TableServiceImpl tableService;
    private final EventServiceImpl eventService;
    private final UserRepository userRepository;


    @Override
    public Ticket save(Ticket ticket) {
        ticketRepository.save(ticket);
        return ticket;
    }

    /**
     * <p><strong>Not implemented</strong></p>
     * @param id
     * @param ticketDto
     * @return
     */
    @Override
    public TicketDto updateById(Long id, TicketDto ticketDto) {
        return null;
    }

    /**
     * <p><strong>Not implemented by frontend</strong></p>
     * <p>This method is intended to retrieve all tickets from the repository and convert them into {@link TicketDto} objects.
     * The method converts each {@link Ticket} entity into a corresponding {@link TicketDto} and returns the list of {@link TicketDto} objects.</p>
     *
     * @return {@link Iterable<TicketDto>} A list of {@link TicketDto} objects representing all tickets in the repository.
     */
    @Override
    public Iterable<TicketDto> findAll() {
        // Convert all tickets to TicketDto and return as a list
        List<TicketDto> ticketDtoList = ticketRepository.findAll().stream().map(t -> new TicketDto(t.getId(), t.getTable().getId(), t.getInvoice().getUser(), t.getPrice(), t.getEvent().getId())).collect(Collectors.toList()); // Collect the mapped objects into a list
        return ticketDtoList; // Return the list of TicketDto
    }



    /**
     *
     * <p>This method retrieves a ticket by its ID and converts it into a {@link TicketDto} object.</p>
     * <p>If the ticket with the specified ID is not found, an {@link AppException} with a {@code NOT_FOUND} status is thrown.</p>
     *
     * @param id {@link Long} The ID of the ticket to retrieve.
     * @return {@link TicketDto} The {@link TicketDto} object containing ticket information.
     * @throws AppException If the ticket with the given ID is not found, a {@code NOT_FOUND} exception is thrown.
     */
    @Override
    public TicketDto findTicketDtoById(Long id) {
        Ticket t = ticketRepository.findById(id).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        TicketDto newTicketDto = new TicketDto(t.getId(), t.getTable().getId(), t.getInvoice().getUser(), t.getPrice(), t.getEvent().getId());
        return newTicketDto;
    }

    /**
     * <p>This method retrieves a ticket by its ID from the repository.</p>
     * <p>If the ticket with the specified ID is not found, an {@link AppException} with a {@code NOT_FOUND} status is thrown.</p>
     *
     * @param id {@link Long} The ID of the ticket to retrieve.
     * @return {@link Ticket} The {@link Ticket} entity object representing the ticket.
     * @throws AppException If the ticket with the given ID is not found, a {@code NOT_FOUND} exception is thrown.
     */
    public Ticket findTicketById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    }

    /**
     * <p>This method deletes a ticket by its ID from the repository.</p>
     * <p>If the ticket with the specified ID exists, it will be removed. If the ticket does not exist, an exception is thrown.</p>
     *
     * @param id {@link Long} The ID of the ticket to delete.
     */
    @Override
    public void deleteById(Long id) {
        ticketRepository.deleteById(id);
    }


    /**
     * <p>This method creates a new ticket for the specified user and event based on the provided details.</p>
     * <p>The method performs the following steps:
     * <ul>
     *     <li>Finds the user by email.</li>
     *     <li>Retrieves the active invoice (cart) for the user.</li>
     *     <li>Checks if there are available seats in the specified table for the ticket.</li>
     *     <li>If seats are available, it creates and saves the specified number of tickets associated with the event and the table.</li>
     *     <li>If no seats are available, an exception is thrown with a {@code BAD_REQUEST} status.</li>
     * </ul>
     * If any errors occur during the process, appropriate exceptions are caught and the corresponding error response is returned.</p>
     *
     * @param email {@link String} The email address of the user for whom the ticket is being created.
     * @param ticketDto {@link TicketDto} The data transfer object containing the ticket details, such as event, table, and amount.
     * @return {@link ResponseEntity} A response entity containing the status of the operation. If the tickets are successfully created, an {@code OK} response is returned. If any errors occur, an error response with the appropriate status and message is returned.
     * @throws AppException If the user cannot be found by email or if there are no available seats for the requested tickets, a custom exception is thrown with the relevant error message and status.
     */
    @Override
    public ResponseEntity createTicket(String email, TicketDto ticketDto) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
            Invoice invoice = user.getActiveInvoice();

            Event event = eventService.findById(ticketDto.getEvent());
            Tafel table = tableService.findById(ticketDto.getTable());

            if (table.checkAvailableSeats(ticketDto.getAmount())) {
                for (int i = 0; i < ticketDto.getAmount(); i++) {
                    Ticket t = Ticket.builder()
                            .price(event.getTicketPrice())
                            .table(table)
                            .invoice(invoice)
                            .event(event)
                            .build();
                    ticketRepository.save(t);
                }
                return ResponseEntity.ok().build(); // Return a success response
            } else {
                throw new AppException("No more available seats", HttpStatus.BAD_REQUEST); // Throw exception if no seats available
            }
        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage()); // Return custom error response
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage()); // Return internal server error response
        }
    }


}
