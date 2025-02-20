package com.example.backend.service.impl;

import com.example.backend.dto.EventDto;
import com.example.backend.exceptions.AppException;
import com.example.backend.model.Event;
import com.example.backend.model.Invoice;
import com.example.backend.model.Tafel;
import com.example.backend.model.Ticket;
import com.example.backend.repository.EventDao;
import com.example.backend.repository.InvoiceRepository;
import com.example.backend.repository.TicketRepository;
import com.example.backend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Service that handles the logic of for the EventController
 */
@Service
public class EventServiceImpl implements EventService {

    private static String IMG_DIR=System.getProperty("user.dir") + "/backend/src/main/resources/images/events";
    private static Path IMGS_PATH= Paths.get(IMG_DIR);

    @Autowired
    private EventDao eventRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private TableServiceImpl tableService;

    private Event getEventById(Long id){
        return eventRepository.findById(id).orElseThrow(()->new AppException("Event not found",HttpStatus.NOT_FOUND));
    }

    @Override
    public Event save(Event event) {
        //when spond events are being created they don't have a layout variable so they must not create
        //a layout
        if(event.getSpondId()==null){
            event.CreateLayout();
        }

        return eventRepository.save(event);
    }

    /**
     * Updates an event by its ID, including its details and associated image.
     * <p>
     * This method retrieves an event using the provided ID and updates its details, such as title, type, location,
     * description, start time, and end time, based on the information in the {@link EventDto} object. It also handles
     * updates to the event's tickets using the {@link #noTicketsUpdate(EventDto, Event)} method. If the event has an
     * existing image, the old image is deleted before saving the new one. The new image is saved to the server, and
     * the event record is updated with the new image filename.
     * </p>
     *
     * @param id the ID of the event to be updated.
     * @param updatedEvent the {@link EventDto} containing the updated event information.
     * @param imageFile the new image file to be associated with the event.
     * @return a {@link ResponseEntity} indicating the success or failure of the update operation.
     * @throws IOException if an error occurs while handling the image file or accessing the file system.
     */
    public ResponseEntity updateById(Long id, EventDto updatedEvent, MultipartFile imageFile) {
        try {
            // Finds event, if it's not found it throws an exception
            Event e = getEventById(id);

            updatedEvent.CreateLayout();

            e.setTitle(updatedEvent.getTitle());
            e.setType(updatedEvent.getType());
            e.setLocation(updatedEvent.getLocation());
            e.setDescription(updatedEvent.getDescription());
            e.setStartTime(dateParsingForEvent(updatedEvent.getStartTime()));
            e.setEndTime(dateParsingForEvent(updatedEvent.getEndTime()));

            noTicketsUpdate(updatedEvent, e);

            if (e.getImg() != null) {
                // Delete the current image if it exists
                if (!Files.exists(IMGS_PATH)) {
                    return ResponseEntity.internalServerError().body(null);
                }

                Path filePath = IMGS_PATH.resolve(e.getImg());
                if (!Files.exists(filePath)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                Files.deleteIfExists(filePath);
            }

            // Save the new image
            // Generate the new filename and construct the file path
            String newFilename = e.getTitle() + "_" + e.getId() + "." +
                    StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            Path newFilePath = IMGS_PATH.resolve(newFilename);

            // Write the file with the new name
            Files.write(newFilePath, imageFile.getBytes());

            // Update the event with the relative path to the image
            e.setImg(newFilename);

            eventRepository.save(e);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    /**
     * Updates an event by its ID without changing the associated image.
     * <p>
     * This method retrieves the event using the provided ID and updates its details based on the values in the
     * {@link EventDto} object. The event's title, type, location, description, start time, and end time are updated.
     * Additionally, the layout and ticket-related properties are handled using the {@link #noTicketsUpdate(EventDto, Event)} method.
     * If the event is successfully updated, it is saved to the database.
     * </p>
     *
     * @param id the ID of the event to be updated.
     * @param updatedEvent the {@link EventDto} containing the updated event information.
     * @return a {@link ResponseEntity} indicating the success or failure of the update operation.
     */
    public ResponseEntity updateById(Long id, EventDto updatedEvent) {
        try {
            // Finds event, if it's not found it throws an exception
            Event e = getEventById(id);

            updatedEvent.CreateLayout();

            e.setTitle(updatedEvent.getTitle());
            e.setType(updatedEvent.getType());
            e.setLocation(updatedEvent.getLocation());
            e.setDescription(updatedEvent.getDescription());
            e.setStartTime(dateParsingForEvent(updatedEvent.getStartTime()));
            e.setEndTime(dateParsingForEvent(updatedEvent.getEndTime()));

            noTicketsUpdate(updatedEvent, e);

            eventRepository.save(e);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @Override
    public Iterable<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findDistinctById(id);
    }

    @Override
    public Event findById(String id) {
        return eventRepository.findDistinctBySpondId(id);
    }

    /**
     * Deletes an event by its ID, checking for open paid invoices before proceeding with the deletion.
     * <p>
     * This method retrieves the event based on the provided ID and then delegates the actual deletion process to the
     * {@link #handleDelete(Event)} method. Before deletion, it checks for any paid invoices that are still open.
     * If any open paid invoices are found, the deletion is prevented. If the event can be deleted, it deletes the event
     * and its associated image file.
     * </p>
     *
     * @param id the ID of the event to be deleted.
     * @return a {@link ResponseEntity} indicating the success or failure of the operation.
     * @throws IOException if there is an issue accessing the file system when attempting to delete the associated image.
     */
    @Override
    public ResponseEntity deleteById(Long id) throws IOException {
        try {
            Event e = getEventById(id);
            return handleDelete(e);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }


    /**
     * Handles the deletion of an event, checking for open paid invoices before proceeding.
     * <p>
     * This method performs the following steps before deleting an event:
     * <ul>
     *     <li>Checks if there are any paid invoices that are still open. If such invoices exist, the event cannot be deleted.</li>
     *     <li>If the event is deletable, it updates the associated invoices and tickets. Paid and closed invoices are deleted, while unpaid invoices have their associated tickets removed.</li>
     *     <li>Deletes the event record and the associated image file.</li>
     * </ul>
     * </p>
     *
     * @param e the event to be deleted.
     * @return a {@link ResponseEntity} indicating the success or failure of the operation.
     * @throws Exception if an error occurs during the deletion process.
     */
    private ResponseEntity handleDelete(Event e) throws IOException {
        boolean hasUnclosedPaidInvoice = e.getTickets().stream()
                .anyMatch(ticket -> ticket.getInvoice().getPaid() && !ticket.getInvoice().getClosed());

        if (hasUnclosedPaidInvoice) {
            return ResponseEntity.internalServerError().body("Cannot be deleted, there are invoices that have been paid and are still open");
        }

        e.getTickets().forEach(ticket -> {
            Invoice i = ticket.getInvoice();
            if (i.getPaid() == false) {
                i.removeTicket(ticket);
                invoiceRepository.save(i);
            } else if (i.getPaid() == true && i.getClosed() == true) {
                invoiceRepository.delete(i);
            }
        });

        //eventRepository.save(e);

        if (!Files.exists(IMGS_PATH)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Path for event images not found");
        }

        Path filePath = IMGS_PATH.resolve(e.getImg());
        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event image not found");
        }

        eventRepository.deleteById(e.getId());
        Files.deleteIfExists(filePath);
        return ResponseEntity.ok().build();

    }


    //Isn't used anywhere
    @Override
    public ResponseEntity deleteById(String id) throws IOException {
        Event e= eventRepository.findDistinctBySpondId(id);
        return handleDelete(e);
    }

    //outdated method for spond, ignore
    @Override
    public void deleteEvent(Event e) {
        eventRepository.delete(e);
    }


    /**
     * Creates a new event and handles the associated image upload.
     * <p>
     * This method creates an event using the provided event details and saves it to the repository.
     * It also processes the uploaded image by saving it to the designated image directory. The event
     * is then updated with the image filename. After saving, the event is returned in the response as a DTO.
     * </p>
     *
     * @param eventDto the data transfer object containing the event details (title, location, description, etc.)
     * @param imageFile the image file to be uploaded and associated with the event
     * @return a ResponseEntity containing the created event's DTO and the HTTP status code:
     *         - {@code HttpStatus.CREATED} if the event is successfully created.
     *         - {@code HttpStatus.BAD_REQUEST} if an error occurs during event creation.
     */
    public ResponseEntity createEvent(EventDto eventDto, MultipartFile imageFile) {
        try {
            // Create a new Event object from the provided data
            Event event = new Event(
                    eventDto.getTitle(),
                    eventDto.getLocation(),
                    eventDto.getDescription(),
                    eventDto.getType(),
                    dateParsingForEvent(eventDto.getStartTime()),
                    dateParsingForEvent(eventDto.getEndTime()),
                    eventDto.getLayout(),
                    eventDto.getSeatsPerTable(),
                    eventDto.getTicketPrice()
            );

            // Save the event to the repository
            eventRepository.save(event);

            // Create the event tables
            tableService.CreateTables(event);

            // Ensure the image directory exists, create it if it doesn't
            if (!Files.exists(IMGS_PATH)) {
                Files.createDirectories(IMGS_PATH);
            }

            // Handle the image file upload
            String newFilename = event.getTitle() + "_" + event.getId() + "." +
                    StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            Path newFilePath = IMGS_PATH.resolve(newFilename);
            Files.write(newFilePath, imageFile.getBytes());

            // Save the image filename to the event
            event.setImg(newFilename);
            eventRepository.save(event);

            // Prepare the saved event data for the response
            EventDto savedEventDto = new EventDto(
                    event.getId(),
                    event.getTitle(),
                    event.getLocation(),
                    event.getType(),
                    event.getDescription(),
                    dateParsingForEventDto(event.getStartTime()),  // Send back in ISO format
                    dateParsingForEventDto(event.getEndTime()),    // Send back in ISO format
                    event.getSeatsPerTable(),
                    event.getImg(),
                    event.getLayout(),
                    event.getTicketPrice(),
                    event.getRijen(),
                    event.getKolommen()
            );

            // Return the created event details in the response
            return new ResponseEntity<>(savedEventDto, HttpStatus.CREATED);

        } catch (Exception e) {
            // Print the stack trace and return a bad request response
            e.printStackTrace();
            return ResponseEntity.badRequest().body(eventDto);
        }
    }



    /**
     * Retrieves an image file for the specified event ID and returns it as a response.
     * The method checks if the image exists in the specified directory and ensures that the file is accessible.
     * If the image is found, it is returned with the appropriate content type.
     * If any issues occur, an appropriate error response is returned.
     *
     * @param id the ID of the event whose image is being requested
     * @return a ResponseEntity containing the image or an error message:
     *         - 200 OK with the image if found and accessible
     *         - 404 Not Found if the image doesn't exist
     *         - 500 Internal Server Error if an error occurs during the process
     */
    public ResponseEntity getImg(Long id) {
        try {
            // Retrieve the event by ID
            Event e = getEventById(id);

            // Check if the images directory exists
            if (!Files.exists(IMGS_PATH)) {
                return ResponseEntity.internalServerError().body(null);
            }

            // Construct the file path for the image
            Path filePath = IMGS_PATH.resolve(e.getImg());

            // Check if the image file exists
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Load the image as a resource
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the resource exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable");
            }

            // Determine the content type of the image
            String contentType = Files.probeContentType(filePath);
            if (StringUtils.isEmpty(contentType)) {
                contentType = "application/octet-stream"; // Fallback content type
            }

            // Return the image with the correct content type
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (AppException a) {
            return ResponseEntity.status(a.getCode()).body(a.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    /**
     * Parses the given string date into a `Date` object based on the format "yyyy-MM-dd HH:mm" in UTC time zone.
     * This method is used to convert a date-time string into a `Date` object for event processing.
     *
     * @param date the date-time string in "yyyy-MM-dd HH:mm" format to be parsed into a `Date` object.
     * @return the `Date` object representing the parsed date and time in UTC.
     * @throws ParseException if the date string does not match the expected format or if parsing fails.
     */
    private Date dateParsingForEvent(String date) throws ParseException {
        // Input format received
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return inputFormat.parse(date);
    }


    /**
     * Parses the given date into a string formatted as "yyyy-MM-dd'T'HH:mm:ss.SSSX" in UTC time zone.
     * This method is used to convert a `Date` object into a standardized date-time string format
     * suitable for use in Event DTOs.
     *
     * @param date the input `Date` object to be formatted.
     * @return a string representing the formatted date in "yyyy-MM-dd'T'HH:mm:ss.SSSX" format, adjusted to UTC.
     * @throws ParseException if the date cannot be parsed or formatted correctly.
     */
    private String dateParsingForEventDto(Date date) throws ParseException {
        // Input format received
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFormat.format(date);
    }


    /**
     * Updates the event details such as ticket price, seats per table, and layout.
     * This method checks whether changes to ticket price, layout, or seats per table
     * can be made based on the current state of the event. If tickets have already been sold,
     * it prevents modifications to those attributes. If no tickets have been sold, it updates
     * the event details and applies changes to the associated tickets and tables accordingly.
     *
     * @param updatedEvent {@link EventDto}the updated event details, including ticket price, layout, and seats per table.
     * @param e {@link Event}the current event that is being updated.
     * @throws AppException if there are already paid tickets for the event, preventing the update.
     */
    private void noTicketsUpdate(EventDto updatedEvent, Event e) {

        // Ensure that ticketPrice is set to zero if it's null (SpondEvent has no ticketPrice)
        if (e.getTicketPrice() == null) {
            e.setTicketPrice(BigDecimal.ZERO);
        }

        // Boolean flag to check if the ticketPrice, layout, or seatsPerTable have been changed
        Boolean noTickets = !(e.getTicketPrice().compareTo(updatedEvent.getTicketPrice()) != 0 ||
                !e.getLayout().equals(updatedEvent.getLayout()) ||
                e.getSeatsPerTable() != updatedEvent.getSeatsPerTable());

        // If no tickets have been sold, proceed with changes
        if (!noTickets) {

            // Get the set of tickets associated with the event
            Set<Ticket> ticketList = e.getTickets();

            // Filter out tickets that have been paid for (not needed because no operation is done on this filtered stream)
            ticketList.stream().filter(ticket -> ticket.getInvoice().getPaid() == true);

            // Check if there are any tickets sold, throw an exception if changes are attempted
            if (!ticketList.isEmpty()) {
                throw new AppException("You cannot change the layout or price when this event has sold tickets", HttpStatus.BAD_REQUEST);
            }

            // Update the ticket price for the event and all associated tickets
            if (e.getTicketPrice() != updatedEvent.getTicketPrice()) {
                e.setTicketPrice(updatedEvent.getTicketPrice());
                e.getTickets().stream().forEach(ticket -> {
                    ticket.setPrice(e.getTicketPrice());
                    ticketRepository.save(ticket);
                });
            }

            // Set default value for seatsPerTable if null (SpondEvent has no seatsPerTable)
            if (e.getSeatsPerTable() == null) {
                e.setSeatsPerTable(0);
            }

            // Update the seats per table for the event and the tables
            if (e.getSeatsPerTable() != updatedEvent.getSeatsPerTable()) {
                e.setSeatsPerTable(updatedEvent.getSeatsPerTable());

                e.getTables().stream().forEach(table -> {
                    table.setSeats(e.getSeatsPerTable());
                    //tableService.save(table);
                });

            }

            // Set default layout if null and create the layout (SpondEvent has no layout)
            if (e.getLayout() == null) {
                e.setLayout("0x0");
                e.CreateLayout();
            }

            // Update the layout of the event and adjust the number of tables accordingly
            if (!e.getLayout().equals(updatedEvent.getLayout())) {

                if ((e.getRijen() * e.getKolommen()) > (updatedEvent.getRijen() * updatedEvent.getKolommen())) {
                    // Remove tables if the new layout requires fewer tables
                    tableService.RemoveTables(e, (e.getRijen() * e.getKolommen() - updatedEvent.getRijen() * updatedEvent.getKolommen()));
                    e.setLayout(updatedEvent.getLayout());
                    e.CreateLayout();
                } else {
                    // Create tables if the new layout requires more tables
                    tableService.CreateTables(e, (e.getRijen() * e.getKolommen() - updatedEvent.getRijen() * updatedEvent.getKolommen()));
                    e.setLayout(updatedEvent.getLayout());
                    e.CreateLayout();
                }
            }
        }
    }

}
