package me.shafi.sem_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "candidates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Candidate {

    @MongoId
    private ObjectId _id;
    private String name;
    private int grade;
    private String section;
    private String image;

    @DocumentReference
    @JsonIgnoreProperties("candidates")
    private Position position;

    private int __v;
}
