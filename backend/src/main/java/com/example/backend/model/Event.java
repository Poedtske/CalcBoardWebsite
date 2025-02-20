package com.example.backend.model;

import com.example.backend.dto.EventDateDto;
import com.example.backend.enums.EventType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.*;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@Table(name="Events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String spondId;

    @NotBlank
    private String title;

    private String img;

    @NotBlank
    private String location;

    @NotNull
    private EventType type=EventType.UNKNOWN;

    @Nullable
    @OneToMany(mappedBy = "event")
    private Set<Ticket> tickets=new HashSet<>();

    @Nullable
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tafel> tables=new ArrayList<>();

    //is used for layout and table creation
    @Nullable
    private int rijen;
    @Nullable
    private int kolommen;
    @Nullable
    private Integer seatsPerTable;
    @Nullable
    private String layout;

    //set price for tickets
    private BigDecimal ticketPrice;

    @Nullable
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    private java.util.Date startTime;

    @Nullable
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "UTC")
    private java.util.Date endTime;

    @Nullable
    private String description;

    public Event() {
    }

    // Full constructor for initializing an Event with all fields
    public Event(String title,
                 String location,
                 String description,
                 EventType type,
                 Date startTime,
                 Date endTime,
                 String layout,
                 int seatsPerTable,
                 BigDecimal ticketPrice) {
        this.spondId = null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
        this.description = description;
        this.type = type;
        this.seatsPerTable = seatsPerTable;
        this.layout=layout;
        this.ticketPrice=ticketPrice;
        CreateLayout();
    }


    // Constructor for creating an event from "Spond" data
    public Event(String spondId, Date startTime, Date endTime, String title, String location, String description) {
        this.spondId = spondId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.location = location;
        this.description = description;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getRijen() {
        return rijen;
    }

    public int getKolommen() {
        return kolommen;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSpondId() {
        return spondId;
    }

    public void setSpondId(String spondId) {
        this.spondId = spondId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
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

    public Long getId() {
        return id;
    }

    public Integer getSeatsPerTable() {
        return seatsPerTable;
    }

    public void setSeatsPerTable(Integer seatsPerTable) {
        this.seatsPerTable = seatsPerTable;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public List<Tafel> getTables() {
        return tables;
    }

    public Tafel AddTable(Tafel t){
        this.tables.add(t);
        return t;
    }

    public Tafel RemoveTable(Tafel t){
        this.tables.remove(t);
        return t;
    }

    public Tafel GetTable(Tafel t){
        this.tables.add(t);
        return tables.stream().filter(table -> table.getId()==t.getId()).findFirst().orElse(null);
    }

    public Ticket AddTicket(Ticket t) {
        this.tickets.add(t);
        return t;
    }

    public Ticket DeleteTicket(Ticket t) {
        this.tickets.remove(t);
        return t;
    }

    public Ticket GetTicket(Ticket t){
        return this.tickets.stream().filter(ticket->ticket.getId()==t.getId()).findFirst().orElse(null);
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    //method specifically for events created by spond
    public void SpondUpdate(Event e){
        this.startTime = e.startTime;
        this.endTime = e.endTime;
        this.title = e.title;
        this.location = e.location;
        if(e.description!=null){
            this.description = e.description;
        }
    }

    //creates rijen & kolommen if there are no tickets
    public void CreateLayout(){
        if(this.tickets.isEmpty()){
            if (LayoutFormatter()) {
                // Height and length successfully added
                LayoutFormatter();
                System.out.println("created layout");
            } else {
                System.out.println("Failed to create layout");
            }
        }
    }

    //takes out the rijen and kolommen from the layout string and assigns them
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
