package pl.ros.keep.core.mongo.files;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.ros.keep.core.mongo.notes.Note;

import java.util.Collection;
import java.util.List;

public interface FileRepository extends MongoRepository<File, String> {

    List<File> findAllByIdInAndStatus(Collection<String> id, String status);
}
