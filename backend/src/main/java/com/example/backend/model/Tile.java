package com.example.backend.model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "map_tiles")
public class Tile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Ensure the ID is generated automatically
    private Integer id;

    @Column(nullable = true)
    private String img;

    @Column(nullable = true)
    private Long tilesId;

    @ElementCollection
    @CollectionTable(name = "tile_words", joinColumns = @JoinColumn(name = "tile_id"))
    @Column(name = "word")
    @Expose
    private List<String> words;

    // Many-to-One: A tile belongs to one map
    @ManyToOne
    @JoinColumn(name = "map_id", nullable = false)
    @Expose(serialize = false)
    private CalcBoardMap map;

    public Tile() {}

    public Tile(Integer id, String img, List<String> words, CalcBoardMap map) {
        this.id = id;
        this.img = img;
        this.words = words;
        this.map = map;
    }

    public Long getTilesId() {
        return tilesId;
    }

    public void setTilesId(Long tilesId) {
        this.tilesId = tilesId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public CalcBoardMap getMap() {
        return map;
    }

    public void setMap(CalcBoardMap map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", img='" + img + '\'' +
                ", words=" + words +
                ", map=" + (map != null ? map.getId() : "null") +
                '}';
    }
}
