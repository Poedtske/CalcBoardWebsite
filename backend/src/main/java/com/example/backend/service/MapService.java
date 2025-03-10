package com.example.backend.service;

import com.example.backend.model.Tile;
import com.example.backend.repository.TilesRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CalcBoardMapRepository;
import com.example.backend.dto.MapSaveRequest;
import com.example.backend.model.User;
import com.example.backend.model.CalcBoardMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class MapService {

    @Autowired
    private final TilesRepository tileRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CalcBoardMapRepository mapRepository;

    @Value("${map.storage.path}")
    private String storagePath;

    public MapService(UserRepository userRepository, CalcBoardMapRepository mapRepository, TilesRepository tileRepository) {
        this.userRepository = userRepository;
        this.mapRepository = mapRepository;
        this.tileRepository = tileRepository;
    }

    public void saveMap(MapSaveRequest request, MultipartFile[] tileImages) throws IOException {
        // Fetch user from the repository
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create and persist the map FIRST
        CalcBoardMap map = new CalcBoardMap();
        map.setGame(request.getGame());
        map.setMapName(request.getMapName());
        map.setCategories(request.getCategories());
        map.setFreeOrNot(false);
        map.setUser(user);

        map = mapRepository.save(map); // Save map before assigning it to tiles

        List<Tile> tilesToSave = new ArrayList<>();

        // Save the tiles and their images
        if (request.getTiles() != null && !request.getTiles().isEmpty()) {
            int tileIndex = 0;
            for (Tile tile : request.getTiles()) {
                tile.setMap(map); // Now the map is persisted, so no transient issue
                tile.setTilesId((long) tileIndex + 1);  // Explicitly set tilesId from request
                tilesToSave.add(tile);
                tileIndex++;
            }
        }

        // Save tiles after assigning a persisted map
        List<Tile> savedTiles = tileRepository.saveAll(tilesToSave);

        // Save images and update tiles
        if (tileImages != null) {
            for (int i = 0; i < savedTiles.size() && i < tileImages.length; i++) {
                MultipartFile tileImageFile = tileImages[i];
                if (tileImageFile != null && !tileImageFile.isEmpty()) {
                    saveTileImageAndUpdateTile(savedTiles.get(i), tileImageFile);
                }
            }
        }
    }

    // Method to save the image and update the tile
    private void saveTileImageAndUpdateTile(Tile tile, MultipartFile tileImageFile) throws IOException {
        // Generate a unique file name using the tile ID
        String fileName = "tile_" + tile.getMap().getMapName() + ".png";

        // Save image to the volume and get the relative path
        String imagePath = saveMapImage(tileImageFile, fileName);

        // Set the image path in the Tile entity
        tile.setImg(imagePath);

        // Save the tile with the image path updated
        tileRepository.save(tile);
    }

    // Save the map image to the volume
    private String saveMapImage(MultipartFile file, String fileName) throws IOException {
        File directory = new File(storagePath);
        if (!directory.exists()) {
            directory.mkdirs();  // Create directory if it doesn't exist
        }

        Path filePath = Path.of(storagePath, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.getFileName().toString();  // Return the relative file path
    }

    // Retrieve map image
    public Resource getMapImage(String fileName) {
        try {
            File file = new File(storagePath, fileName);
            if (!file.exists()) {
                throw new RuntimeException("File not found");
            }
            return new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error retrieving image", e);
        }
    }

    // Get all maps
    public List<CalcBoardMap> getAllMaps() {
        return mapRepository.findAll();
    }

    // Get maps by user
    public List<CalcBoardMap> getUserMaps(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapRepository.findByUser(user);
    }

    // Toggle the privacy of a map
    public void toggleMapPrivacy(Integer mapId, String username) {
        CalcBoardMap map = mapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found"));

        if (!map.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Unauthorized to edit this map");
        }

        map.setFreeOrNot(!map.getFreeOrNot());
        mapRepository.save(map);
    }

    // Update map description
    public void updateMapDescription(Integer mapId, String username, String newDescription) {
        CalcBoardMap map = mapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found"));
        if (!map.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to update this map");
        }
        map.setDescription(newDescription);
        mapRepository.save(map);
    }
}
