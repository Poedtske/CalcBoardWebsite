package com.example.yourproject.model;

import jakarta.persistence.*;

@Entity
@Table(name = "calc_board_maps")
public class CalcBoardMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String game;

    @Column(name = "map_name", nullable = false)
    private String mapName;

    @Column(nullable = false)
    private String img;

    // Constructors
    public CalcBoardMap() {
    }

    public CalcBoardMap(int id, String game, String mapName, String img) {
        this.id = id;
        this.game = game;
        this.mapName = mapName;
        this.img = img;
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

    // ToString method for debugging purposes
    @Override
    public String toString() {
        return "CalcBoardMap{" +
                "id=" + id +
                ", game='" + game + '\'' +
                ", mapName='" + mapName + '\'' +
                ", img='" + img + '\'' +
                '}';
    }
}
