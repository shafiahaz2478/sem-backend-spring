package me.shafi.sem_backend.service;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import me.shafi.sem_backend.models.Votes;
import me.shafi.sem_backend.models.vote.VoteInfo;
import me.shafi.sem_backend.models.vote.VoterInfo;
import me.shafi.sem_backend.respository.VoteRepository;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    VoteRepository voteRepository;



    public String castVote(Map<String, Object> voterInfoMap, List<Map<String, String>> votesList) {
        try {
            // Extract voterInfo details
            String name = (String) voterInfoMap.get("name");
            String grade = (String) voterInfoMap.get("grade");
            String section = (String) voterInfoMap.get("section");
            boolean isStaff = (boolean) voterInfoMap.get("isStaff");

            // Generate ObjectId for voterInfo if needed
            ObjectId voterInfoId = new ObjectId();

            // Map votesList to VoteInfo objects
            List<VoteInfo> voteInfos = votesList.stream()
                    .map(voteInfoMap -> {
                        ObjectId positionId = new ObjectId( voteInfoMap.get("positionId"));
                        ObjectId candidateId = new ObjectId( voteInfoMap.get("candidateId"));
                        ObjectId id = new ObjectId(); // Generate new ObjectId

                        return new VoteInfo(positionId, candidateId, id.toHexString()); // Convert ObjectId to string
                    })
                    .collect(Collectors.toList());

            // Create Votes object
            Votes vote = new Votes();
            vote.setVoterInfo(new VoterInfo(name, grade, section, isStaff, voterInfoId.toHexString()));
            vote.setVotes(voteInfos);

            // Save Votes object to MongoDB
            Votes savedVote = voteRepository.save(vote);

            // Construct response JSON
            String responseBuilder = "{\"vote\":" + savedVote.toString() + ",\"message\":\"Vote casted\"}";

            return responseBuilder;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage()); // Handle error in controller
        }
    }

    public String getResults() {

        List<Bson> pipelineStudent = Arrays.asList(
                Aggregates.match(Filters.eq("voterInfo.isStaff", false)),
                Aggregates.unwind("$votes"),
                Aggregates.group(new Document("position", "$votes.position")
                                .append("candidate", "$votes.candidate"),
                        Accumulators.sum("totalVotes", 1)),
                Aggregates.lookup("candidates", "_id.candidate", "_id", "candidate"),
                Aggregates.unwind("$candidate"),
                Aggregates.lookup("positions", "_id.position", "_id", "position"),
                Aggregates.unwind("$position"),
                Aggregates.project(new Document("candidate", "$candidate.name")
                        .append("position", "$position.name")
                        .append("totalVotes", 1)
                        .append("_id", new Document("position", "$_id.position")
                                .append("candidate", "$_id.candidate"))),
                Aggregates.sort(new Document("position.name", 1).append("totalVotes", -1))
        );

        List<Bson> pipelineStaff = Arrays.asList(
                Aggregates.match(Filters.eq("voterInfo.isStaff", true)),
                Aggregates.unwind("$votes"),
                Aggregates.group(new Document("position", "$votes.position")
                                .append("candidate", "$votes.candidate"),
                        Accumulators.sum("totalVotes", 1)),
                Aggregates.lookup("candidates", "_id.candidate", "_id", "candidate"),
                Aggregates.unwind("$candidate"),
                Aggregates.lookup("positions", "_id.position", "_id", "position"),
                Aggregates.unwind("$position"),
                Aggregates.project(new Document("candidate", "$candidate.name")
                        .append("position", "$position.name")
                        .append("totalVotes", 1)
                        .append("_id", new Document("position", "$_id.position")
                                .append("candidate", "$_id.candidate"))),
                Aggregates.sort(new Document("position.name", 1).append("totalVotes", -1))
        );

        MongoCollection<Document> collection = mongoTemplate.getCollection("votes");


        AggregateIterable<Document> studentResults = collection.aggregate(pipelineStudent);
        AggregateIterable<Document> staffResults = collection.aggregate(pipelineStaff);

        List<Document> studentResultsList = new ArrayList<>();
        studentResults.into(studentResultsList);

        List<Document> staffResultsList = new ArrayList<>();
        staffResults.into(staffResultsList);


        Document response = new Document();
        response.put("studentResults", studentResultsList.isEmpty() ? new ArrayList<>() : studentResultsList);
        response.put("staffResults", staffResultsList.isEmpty() ? new ArrayList<>() : staffResultsList);

        return response.toJson();
    }

}
