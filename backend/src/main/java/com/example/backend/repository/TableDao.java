package com.example.backend.repository;

import com.example.backend.model.Event;
import com.example.backend.model.Tafel;
import org.springframework.data.repository.CrudRepository;

public interface TableDao extends CrudRepository<Tafel,Long> {
}
