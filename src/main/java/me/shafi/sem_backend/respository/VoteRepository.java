package me.shafi.sem_backend.respository;

import me.shafi.sem_backend.models.Votes;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends MongoRepository<Votes, String> {
}
