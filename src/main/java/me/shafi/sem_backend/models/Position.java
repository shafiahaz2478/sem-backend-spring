package me.shafi.sem_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;


import java.util.List;

@Document(collection = "positions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {

    @MongoId
    ObjectId _id;
    String name;
    @DocumentReference
    @JsonIgnoreProperties("position")
    List<Candidate>  candidates;
    int __v;
}
