package com.example.backend.service;

import com.example.backend.model.Event;
import com.example.backend.model.Product;
import com.example.backend.model.Tafel;

import java.util.List;

public interface TableService {
    Tafel save(Tafel tafel);

    Tafel updateById(Long id, Tafel tafel);

    Iterable<Tafel> findAll();

    Tafel findById(Long id);

    void deleteById(Long id);

    void CreateTables(Event e);

    void CreateTables(Event e, int amount);

    void RemoveTables(Event e, int amount);
}
