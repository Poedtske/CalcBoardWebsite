package com.example.backend.service;

import com.example.backend.dto.MapSaveRequestDto;
import com.example.backend.dto.TileDto;
import com.example.backend.model.Tile;
import com.example.backend.repository.TilesRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CalcBoardMapRepository;
import com.example.backend.dto.MapSaveRequest;
import com.example.backend.model.User;
import com.example.backend.model.CalcBoardMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${spring.map.storage.path}")
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
        saveJsonWithDTO(request, request.getMapName());

    }

    // Method to save the image and update the tile
    private void saveTileImageAndUpdateTile(Tile tile, MultipartFile tileImageFile) throws IOException {
        // Generate a unique file name using the tile ID
        String fileName = "tile_" + tile.getTilesId() + ".png";

        // Save image under the correct map folder
        String imagePath = saveMapImage(tileImageFile, fileName, tile.getMap().getMapName());

        // Set the image path in the Tile entity
        tile.setImg(imagePath);

        // Save the tile with the updated image path
        tileRepository.save(tile);
    }

    public void saveJsonWithDTO(MapSaveRequest request, String mapName) throws IOException {
        // Create the DTO object
        MapSaveRequestDto requestDTO = new MapSaveRequestDto();

        // Map the basic fields
        requestDTO.setGame(request.getGame());
        requestDTO.setMapName(request.getMapName());
        requestDTO.setId(request.getId());  // Assuming 'id' is part of your MapSaveRequest class
        requestDTO.setCategories(request.getCategories());

        // Map the Tiles
        List<TileDto> tileDTOs = new ArrayList<>();
        for (Tile tile : request.getTiles()) {
            TileDto tileDTO = new TileDto();
            tileDTO.setId(tile.getTilesId());       // Assuming each Tile has an ID
            tileDTO.setImg(tile.getImg() != null ? tile.getImg() : ""); // Set Img, or empty string if null
            tileDTO.setWords(tile.getWords() != null && !tile.getWords().isEmpty() ? tile.getWords() : new ArrayList<>(List.of(""))); // Default to empty array if no words
            tileDTOs.add(tileDTO);
        }
        requestDTO.setTiles(tileDTOs);

        // Serialize the DTO to JSON using Gson (or Jackson if preferred)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(requestDTO);

        // Construct the path to save the JSON file under the same directory as the images folder
        File mapFolder = new File(storagePath, mapName);
        File imagesFolder = new File(mapFolder, "images");

        // Ensure the images folder exists (this also ensures map folder exists)
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs(); // Create images folder if missing
        }

        // Define the JSON file path in the same directory as the images folder
        Path jsonFilePath = Paths.get(imagesFolder.getParent(), mapName + ".json");

        // Write the JSON to the file
        try (FileWriter fileWriter = new FileWriter(jsonFilePath.toFile())) {
            fileWriter.write(json);
        }

        System.out.println("JSON file saved successfully to: " + jsonFilePath.toString());
    }


    // Save the map image to the correct map folder
    private String saveMapImage(MultipartFile file, String fileName, String mapName) throws IOException {
        // Define the path for this specific map folder
        File mapFolder = new File(storagePath, mapName);
        File mapImagesFolder = new File(mapFolder, "images");

        // Ensure the folders exist
        if (!mapImagesFolder.exists()) {
            mapImagesFolder.mkdirs(); // Create /mapName/images/ folder if missing
        }

        // Define the path for the image file
        Path filePath = Paths.get(mapImagesFolder.getAbsolutePath(), fileName);

        // Copy the uploaded file to the target location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path to be saved in the database (e.g., "mapName/images/fileName.png")
        return mapName + "/images/" + fileName;
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
