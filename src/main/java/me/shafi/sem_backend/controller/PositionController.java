package me.shafi.sem_backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import me.shafi.sem_backend.models.Position;
import me.shafi.sem_backend.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    PositionService service;


    @GetMapping
    public ResponseEntity<String> getPositions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(service.findAllPositions(), headers, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<String> createPosition(@RequestBody Position newPosition) {
        try {
            String response = service.createPosition(newPosition);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create position: " + e.getMessage());
        }
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<?> deletePosition(@PathVariable String positionId) {
        String message = service.deletePositionById(positionId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
