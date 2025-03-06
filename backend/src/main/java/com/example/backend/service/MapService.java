package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CalcBoardMapRepository;
import com.example.backend.dto.MapSaveRequest;
import com.example.backend.model.User;
import com.example.backend.model.CalcBoardMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class MapService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CalcBoardMapRepository mapRepository;

    @Value("${map.storage.path}")
    private String storagePath;  // This will get the path from environment variable

    public MapService(UserRepository userRepository, CalcBoardMapRepository mapRepository) {
        this.userRepository = userRepository;
        this.mapRepository = mapRepository;
    }

    // Save map and image
    public void saveMap(MapSaveRequest request, MultipartFile imageFile) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a new map entity
        CalcBoardMap map = new CalcBoardMap();
        map.setGame(request.getMap().getGame()); // Assuming 'getGame' is a method in your map DTO
        map.setMapName(request.getMap().getMapName()); // Assuming 'getMapName' is a method in your map DTO
        map.setFreeOrNot(false);  // Default value, could be updated later
        map.setUser(user);

        // Save the image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Generate a file name based on timestamp and original file name
                String fileName = "map_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                // Save image and get the file path
                String imagePath = saveMapImage(imageFile, fileName);
                // Set the image path in the map
                map.setImg(imagePath);
            } catch (IOException e) {
                throw new RuntimeException("Error saving image", e);
            }
        }

        // Save the map object in the database
        mapRepository.save(map);
    }

    // Helper method to save the image to the volume (Docker-mounted directory)
    private String saveMapImage(MultipartFile file, String fileName) throws IOException {
        // Ensure the storage directory exists within the mounted volume path
        File directory = new File(storagePath);
        if (!directory.exists()) {
            directory.mkdirs(); // Create directory if it doesn't exist
        }

        // Define the file path where the image will be saved
        Path filePath = Path.of(storagePath, fileName);
        // Copy the file data to the storage location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        // Return the relative path to the saved image (inside Docker volume)
        return filePath.getFileName().toString();  // Save only the file name in DB, not the full path
    }

    // Retrieve the saved map image
    public Resource getMapImage(String fileName) {
        try {
            // Define the path to the image in the volume
            File file = new File(storagePath, fileName);
            // Check if the file exists
            if (!file.exists()) {
                throw new RuntimeException("File not found");
            }
            // Return the image as a resource
            return new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error retrieving image", e);
        }
    }

    // Get all maps
    public List<CalcBoardMap> getAllMaps() {
        return mapRepository.findAll();
    }

    // Get maps belonging to a specific user
    public List<CalcBoardMap> getUserMaps(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapRepository.findByUser(user);
    }

    // Toggle the privacy setting for a map (free or not)
    public void toggleMapPrivacy(Integer mapId, String username) {
        CalcBoardMap map = mapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found"));

        // Ensure the user is authorized to modify this map
        if (!map.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Unauthorized to edit this map");
        }

        // Toggle the privacy setting
        map.setFreeOrNot(!map.getFreeOrNot());
        mapRepository.save(map);
    }
}
