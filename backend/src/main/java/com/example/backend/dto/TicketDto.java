package com.example.backend.dto;

import com.example.backend.model.Event;
import com.example.backend.model.Tafel;
import com.example.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private Long id;
    private Long table;
    private UserDto owner;
    private BigDecimal price;
    private Long event;
    private int amount;

    public TicketDto(Long table, User owner, BigDecimal price, Long event) {
        this.table = table;
        this.owner= new UserDto(owner.getFirstName(),owner.getLastName(),owner.getEmail());
        this.price = price;
        this.event = event;
    }

    //to portray simple ticket information
    public TicketDto(Long table, BigDecimal price, Long event) {
        this.table = table;
        this.price = price;
        this.event = event;
    }

    //to create a ticket
    public TicketDto(Long table, BigDecimal price, Long event, int amount) {
        this.table = table;
        this.price = price;
        this.event = event;
        this.amount=amount;
    }

    public TicketDto(Long id, Long table, User owner, BigDecimal price, Long event) {
        this.id = id;
        this.table = table;
        this.owner= new UserDto(owner.getFirstName(),owner.getLastName(),owner.getEmail());
        this.price = price;
        this.event = event;
    }
}
