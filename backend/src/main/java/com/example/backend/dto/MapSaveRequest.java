package com.example.backend.dto;

import com.example.backend.model.CalcBoardMap;

public class MapSaveRequest {
    private Long userId;
    private CalcBoardMap map;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CalcBoardMap getMap() {
        return map;
    }

    public void setMap(CalcBoardMap map) {
        this.map = map;
    }
}