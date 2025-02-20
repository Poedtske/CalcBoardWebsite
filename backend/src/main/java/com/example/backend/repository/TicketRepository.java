package com.example.backend.repository;

import com.example.backend.model.Ticket;
import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {

    public Optional<Ticket> findById(Long id);
}
