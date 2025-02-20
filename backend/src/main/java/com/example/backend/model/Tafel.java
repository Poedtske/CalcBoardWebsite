package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Tables")
public class Tafel {

    //@EmbeddedId TableId id;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    @OneToMany(mappedBy = "table")
    private Set<Ticket> tickets;


    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "event_id", referencedColumnName = "id", insertable = true, updatable = false, nullable = false)
    private Event event;

    //was originally used for defining the widht and height of the table on the layout page
    private Integer width;
    private Integer height;

    @Nullable
    private int seats;

    public Tafel() {
    }

    public Tafel(Event event, int seats) {
        this.seats = seats;
        this.event=event;
        this.width=100;
        this.height=100;
    }
    public Tafel(Event event, int seats,int width,int height) {
        this.seats = seats;
        this.event=event;
        this.width=width;
        this.height=height;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public Long getId(){
        return id;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public Event getEvent() {
        return event;
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

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public boolean checkAvailableSeats(int amount){
        return (seats-(tickets.size()+amount))>=0;
    }
}
