package me.shafi.sem_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.shafi.sem_backend.models.Candidate;
import me.shafi.sem_backend.models.Position;
import me.shafi.sem_backend.respository.CandidateRepository;
import me.shafi.sem_backend.respository.PositionRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    CandidateService candidateService;

    public String findAllPositions() {
        List<String> positionStrings = positionRepository.findAll().stream()
                .map(this::transformPosition)
                .collect(Collectors.toList());

        return "{\"positions\": [" + String.join(",", positionStrings) + "]}";
    }
    public String createPosition(Position newPosition) {
        return "{\"position\": " + transformPosition(positionRepository.save(newPosition)) + ", \"message\": \"Position added\"}";
    }

    private String transformPosition(Position position) {
        List<Candidate> candidates = position.getCandidates();
        if (candidates == null) {
            candidates = Collections.emptyList(); // or any other appropriate handling
        }

        List<ObjectId> candidateIds = candidates.stream()
                .map(Candidate::get_id)
                .toList();

        return String.format(
                "{\"_id\": \"%s\", \"name\": \"%s\", \"candidates\": [%s], \"__v\": %d}",
                position.get_id(),
                position.getName(),
                String.join(",", candidateIds.stream().map(id -> "\"" + id.toString() + "\"").collect(Collectors.toList())),
                position.get__v()
        );
    }

    public Position getPositionById(String id) {
        return positionRepository.findAll().stream().filter(position -> id.equals(position.get_id())).toList().get(0);
    }
    public String deletePositionById(String positionId) {
        List<Candidate> candidates = positionRepository.findById(new ObjectId(positionId)).get().getCandidates();
        for (Candidate candidate : candidates) {
            candidateService.deleteCandidateById(candidate.get_id().toString());
        }
        positionRepository.deleteById(new ObjectId(positionId));
        return "{\"message\":\"Position removed\"}";
    }
}
