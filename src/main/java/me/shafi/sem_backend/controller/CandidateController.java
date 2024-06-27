package me.shafi.sem_backend.controller;

import me.shafi.sem_backend.models.Candidate;
import me.shafi.sem_backend.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin
@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    @Autowired
    CandidateService service;


    private static String UPLOAD_DIR = "uploads/";

    @GetMapping
    public ResponseEntity<String> getCandidates() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        return new ResponseEntity<>(service.findAllCandidates(), headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createCandidate(
            @RequestParam("name") String name,
            @RequestParam("grade") int grade,
            @RequestParam("section") String section,
            @RequestParam("positionId") String positionId,
            @RequestParam("image") MultipartFile imageFile) {

        String imageName = null;
        if (!imageFile.isEmpty()) {
            try {
                String fileExtension = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf("."));
                imageName = System.currentTimeMillis() + "-" + imageFile.hashCode() + fileExtension;
                Path path = Paths.get(UPLOAD_DIR + imageName);
                Files.createDirectories(path.getParent());
                Files.write(path, imageFile.getBytes());
            } catch (IOException e) {
                return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        String response = service.createCandidate(name, grade, section, positionId, imageName);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable String id) {
        Candidate candidate = service.findCandidateById(id);
        if (candidate == null || candidate.getImage() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir); // Create the directory if it doesn't exist
            }
            Path path = Paths.get(UPLOAD_DIR).resolve(candidate.getImage());
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{candidateId}")
    public ResponseEntity<?> deleteCandidate(@PathVariable String candidateId) {
        String message = service.deleteCandidateById(candidateId);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
