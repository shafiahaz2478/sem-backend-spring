package me.shafi.sem_backend.respository;


import me.shafi.sem_backend.models.Position;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends MongoRepository<Position, ObjectId> {
}
