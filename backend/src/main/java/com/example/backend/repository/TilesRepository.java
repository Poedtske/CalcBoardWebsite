package com.example.backend.repository;


import com.example.backend.model.Tile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TilesRepository extends JpaRepository<Tile, Integer> {
}
