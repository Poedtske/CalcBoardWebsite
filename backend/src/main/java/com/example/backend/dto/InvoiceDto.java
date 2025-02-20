package com.example.backend.dto;

import com.example.backend.model.Product;
import com.example.backend.model.Ticket;
import com.example.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Long id;
    private Boolean paid;
    private Boolean confirmed;
    private Boolean done;
    private String description;
    private UserDto user;
    private List<Product> productList;
    private Set<Ticket> ticketSet;

    public InvoiceDto(Long id, UserDto user) {
        this.id = id;
        this.user = user;
    }

    public InvoiceDto(Long id, Boolean paid, Boolean confirmed,Boolean done, String description, User user) {
        this.id = id;
        this.paid = paid;
        this.description = description;
        this.confirmed=confirmed;
        this.done=done;
        this.user = new UserDto(user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public InvoiceDto(UserDto user, List<Product> productList, Set<Ticket> ticketSet) {
        this.user = user;
        this.productList = productList;
        this.ticketSet = ticketSet;
    }
}
