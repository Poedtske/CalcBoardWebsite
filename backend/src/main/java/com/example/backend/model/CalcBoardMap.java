package com.example.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.backend.model.User;


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

    @Column(nullable = false)
    private boolean freeOrNot;

    // Constructors
    public CalcBoardMap() {
    }

    public CalcBoardMap(int id, String game, String mapName, String img, boolean freeOrNot) {
        this.id = id;
        this.game = game;
        this.mapName = mapName;
        this.img = img;
        this.freeOrNot = freeOrNot;
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

    // many to one because map can belong to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getter and Setter for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
