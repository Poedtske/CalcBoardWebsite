package com.example.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.backend.model.User;

import java.util.List;


@Entity
@Table(name = "calc_board_maps")
public class CalcBoardMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private String game;

    @JsonProperty("name")
    @Column(name = "map_name", nullable = true)
    private String mapName;

    @Column(nullable = true)
    private String img;

    @Column(nullable = true, length = 500)
    private String description;

    @Column(nullable = false)
    private boolean freeOrNot;

    // Many-to-One: A map belongs to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // NEW: Store categories as a list in a separate table
    @ElementCollection
    @CollectionTable(name = "map_categories", joinColumns = @JoinColumn(name = "map_id"))
    @Column(name = "category")
    private List<String> categories;

    // NEW: One-to-Many relationship with Tile
    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tile> tiles;

    // Constructors
    public CalcBoardMap() {}

    public CalcBoardMap(int id, String game, String mapName, String img, boolean freeOrNot, String description, List<String> categories, List<Tile> tiles) {
        this.id = id;
        this.game = game;
        this.mapName = mapName;
        this.img = img;
        this.freeOrNot = freeOrNot;
        this.description = description;
        this.categories = categories;
        this.tiles = tiles;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean getFreeOrNot() {
        return freeOrNot;
    }

    public void setFreeOrNot(boolean freeOrNot) {
        this.freeOrNot = freeOrNot;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }

    public boolean isFreeOrNot() {
        return freeOrNot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CalcBoardMap{" +
                "id=" + id +
                ", game='" + game + '\'' +
                ", mapName='" + mapName + '\'' +
                ", img='" + img + '\'' +
                ", categories=" + categories +
                ", tiles=" + tiles +
                '}';
    }
}
