package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CalcBoardMapRepository;
import com.example.backend.dto.MapSaveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backend.model.User;
import com.example.backend.model.CalcBoardMap;

import java.util.List;

@Service
public class MapService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalcBoardMapRepository mapRepository;


    public void saveMap(MapSaveRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CalcBoardMap map = new CalcBoardMap();
        map.setGame(request.getMap().getGame());
        map.setMapName(request.getMap().getMapName());
        map.setImg(request.getMap().getImg());
        map.setFreeOrNot(false);
        map.setUser(user);  // Link the user!

        mapRepository.save(map);
    }

    public List<CalcBoardMap> getAllMaps() {
        return mapRepository.findAll();
    }

    public List<CalcBoardMap> getUserMaps(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapRepository.findByUser(user);
    }

    public void toggleMapPrivacy(Integer mapId, String username) {
        CalcBoardMap map = mapRepository.findById(mapId)
                .orElseThrow(() -> new RuntimeException("Map not found"));

        if (!map.getUser().getEmail().equals(username)) {
            throw new RuntimeException("Unauthorized to edit this map");
        }

        map.setFreeOrNot(!map.getFreeOrNot()); // Toggle privacy
        mapRepository.save(map);
    }
}
