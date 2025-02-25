package com.example.backend.repository;

import java.util.List;
import com.example.backend.model.User;
import com.example.backend.model.CalcBoardMap;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalcBoardMapRepository extends JpaRepository<CalcBoardMap, Integer> {
    List<CalcBoardMap> findByUser(User user);
}