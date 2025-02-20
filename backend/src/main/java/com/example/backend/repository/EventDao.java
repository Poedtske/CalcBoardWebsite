package com.example.backend.repository;

import com.example.backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventDao extends JpaRepository<Event,Long> {
    public Event findDistinctBySpondId(String spondId);
    public Event findDistinctById(Long id);
}
