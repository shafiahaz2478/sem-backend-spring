package me.shafi.sem_backend.controller;

import me.shafi.sem_backend.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    VoteService service;

    @GetMapping("/results")
    public ResponseEntity<String> getResults() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(service.getResults(), headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> castVote(@RequestBody Map<String, Object> requestBody) {
        try {
            // Extract voterInfo and votes from the request body
            Map<String, Object> voterInfo = (Map<String, Object>) requestBody.get("voterInfo");
            List<Map<String, String>> votes = (List<Map<String, String>>) requestBody.get("votes");

            // Call voteService to process the vote
            String response = service.castVote(voterInfo, votes);

            // Return the response with status 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Return error response with status 400 (Bad Request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
