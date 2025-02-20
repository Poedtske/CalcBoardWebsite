package com.example.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "Tickets")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "table_id", referencedColumnName = "id", insertable = true, updatable = true)
    private Tafel table;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", insertable = true, updatable = false, nullable = false)
    private Invoice invoice;

    private BigDecimal price;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "event_id",referencedColumnName = "id")
    private Event event;

    public Ticket(Tafel table, Invoice invoice, BigDecimal price, Event event) {
        this.table = table;
        this.invoice = invoice;
        this.price = price;
        this.event = event;
    }

    public void setTable(Tafel table) {
        this.table = table;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return "Ticket "+getId()+" from event: "+getEvent().getTitle();
    }

    public Tafel getTable() {
        return table;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return id != null && id.equals(ticket.id); // Compare based on id
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0; // Hash based on id
    }
}
