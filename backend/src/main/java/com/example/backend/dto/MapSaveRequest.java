package com.example.backend.dto;

import com.example.backend.model.Tile;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class MapSaveRequest {

    @JsonProperty("Game")
    private String game;

    @JsonProperty("Categories")
    private List<String> categories;

    @JsonProperty("Id")
    private Long id;

    @JsonProperty("MapName")
    private String mapName;

    @JsonProperty("Tiles")
    private List<Tile> tiles;

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }
}