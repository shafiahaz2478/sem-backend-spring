package me.shafi.sem_backend.service;

import me.shafi.sem_backend.models.Candidate;
import me.shafi.sem_backend.models.Position;
import me.shafi.sem_backend.respository.CandidateRepository;
import me.shafi.sem_backend.respository.PositionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CandidateService {
    @Autowired
    CandidateRepository candidateRepository;

    @Autowired
    PositionRepository positionRepository;

    public String findAllCandidates() {
        List<String> candidateStrings = candidateRepository.findAll().stream()
                .map(this::transformCandidate)
                .collect(Collectors.toList());

        return "{\"candidates\": [" + String.join(",", candidateStrings) + "]}";
    }

    private String transformCandidate(Candidate candidate) {
        Position position = candidate.getPosition();

        List<String> candidateIds = position.getCandidates().stream()
                .map(c -> c.get_id().toString())
                .toList();

        String positionString = String.format(
                "{\"_id\": \"%s\", \"name\": \"%s\", \"candidates\": [%s], \"__v\": %d}",
                position.get_id().toString(),
                position.getName(),
                String.join(",", candidateIds.stream().map(id -> "\"" + id + "\"").collect(Collectors.toList())),
                position.get__v()
        );

        return String.format(
                "{\"_id\": \"%s\", \"name\": \"%s\", \"grade\": %d, \"section\": \"%s\", \"position\": %s, \"image\": \"%s\", \"__v\": %d}",
                candidate.get_id().toString(),
                candidate.getName(),
                candidate.getGrade(),
                candidate.getSection(),
                positionString,
                candidate.getImage(),
                candidate.get__v()
        );
    }


    public String createCandidate(String name, int grade, String section, String positionId, String imageName) {
        ObjectId positionObjectId = new ObjectId(positionId);
        Optional<Position> positionOpt = positionRepository.findById(positionObjectId);
        if (!positionOpt.isPresent()) {
            throw new RuntimeException("Position not found");
        }

        Position position = positionOpt.get();
        Candidate candidate = new Candidate();
        candidate.setName(name);
        candidate.setGrade(grade);
        candidate.setSection(section);
        candidate.setPosition(position);
        candidate.setImage(imageName);

        candidate = candidateRepository.save(candidate);

        position.getCandidates().add(candidate);
        positionRepository.save(position);

        String response = String.format(
                "{\"candidate\": {\"name\": \"%s\", \"grade\": %d, \"section\": \"%s\", \"position\": \"%s\", \"_id\": \"%s\", \"image\": \"%s\", \"__v\": %d}, \"message\": \"Candidate added\"}",
                candidate.getName(),
                candidate.getGrade(),
                candidate.getSection(),
                candidate.getPosition().get_id(),
                candidate.get_id().toString(),
                candidate.getImage(),
                candidate.get__v()
        );

        return response;
    }
    public Candidate findCandidateById(String id) {
        return candidateRepository.findById(new ObjectId(id)).orElse(null);
    }
    public String deleteCandidateById(String candidateId) {
        Candidate candidate = candidateRepository.findById(new ObjectId( candidateId))
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Delete candidate's image file if it exists
        if (candidate.getImage() != null && !candidate.getImage().isEmpty()) {
            File imageFile = new File("uploads/" + candidate.getImage());
            if (imageFile.exists()) {
                imageFile.delete();
            }
        }
        candidateRepository.deleteById(new ObjectId(candidateId));
        return "{\"message\":\"Candidate removed\"}";
    }
}

