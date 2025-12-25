package pl.ros.keep.core.mongo.notes;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {

    List<Note> findAllByCreatedBy_IdAndStatus(Long createdById, String status);
}
