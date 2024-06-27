package me.shafi.sem_backend.models.vote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.MongoId;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class VoterInfo {
    private String name;
    private String grade;
    private String section;
    private boolean isStaff;
    @Id
    private String id; // MongoDB ObjectId will be automatically generated



}
