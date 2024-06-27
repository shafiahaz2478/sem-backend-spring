package me.shafi.sem_backend.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.shafi.sem_backend.models.vote.VoteInfo;
import me.shafi.sem_backend.models.vote.VoterInfo;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "votes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Votes {
    @Id
    String _id;
    private VoterInfo voterInfo;
    private List<VoteInfo> votes;
    private int __v;
    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return "{}";
        }
    }
}

