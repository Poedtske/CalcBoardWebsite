package com.example.backend.dto;

import java.util.List;

public class MapSaveRequestDto {
    private String Game;
    private List<String> Categories;
    private Long Id;
    private String MapName;
    private List<TileDto> Tiles;

    // Getters and Setters
    public String getGame() { return Game; }
    public void setGame(String game) { this.Game = game; }
    public List<String> getCategories() { return Categories; }
    public void setCategories(List<String> categories) { this.Categories = categories; }
    public Long getId() { return Id; }
    public void setId(Long id) { this.Id = id; }
    public String getMapName() { return MapName; }
    public void setMapName(String mapName) { this.MapName = mapName; }
    public List<TileDto> getTiles() { return Tiles; }
    public void setTiles(List<TileDto> tiles) { this.Tiles = tiles; }
}
