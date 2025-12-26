package pl.ros.keep.core.mongo.files;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.ros.keep.commons.crud.entities.AbstractCustomEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = "files")
public class File extends AbstractCustomEntity<String> {
    @Id
    private String id;
    private String storageLocation;
    private String key;
    private String contentType;
    private String filename;
    private Long size;
}
