package me.shafi.sem_backend.models.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteInfo {
    ObjectId position;
    ObjectId candidate;
    @Id
    private String id; // MongoDB ObjectId will be automatically generated

}
