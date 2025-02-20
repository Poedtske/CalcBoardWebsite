package com.example.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "Invoice_Products",
            joinColumns =@JoinColumn(name = "invoice_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products=new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new HashSet<>();

    @Nullable
    private String description;
    //event stages:
    //if user claims to have paid
    private Boolean paid = false;

    //if admin confirmed payment
    private Boolean confirmed= false;

    //if the invoice has been entirely completed, ex. client "payed" for product, payment is confirmed by admin,
    //after product is delived the invoice is closed
    private Boolean closed =false;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    /*@JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;*/

    public Invoice() {
    }

    public Invoice(User u) {
        this.setUser(u);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean done) {
        this.closed = done;
    }

    public Long getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Product addProduct(Product p){
        this.products.add(p);
        return p;
    }

    public Product removeProduct(Product p){
        this.products.remove(p);
        return p;
    }

    public Ticket addTicket(Ticket ticket) {
        tickets.add(ticket);
        return ticket;
    }
    public Ticket removeTicket(Ticket ticket) {
        tickets.remove(ticket);
        return ticket;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return id != null && id.equals(invoice.id); // Compare by id if it's not null
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0; // Use id hash code, fallback to 0 if id is null
    }
}
