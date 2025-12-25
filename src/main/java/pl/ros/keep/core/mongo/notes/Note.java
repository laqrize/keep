package pl.ros.keep.core.mongo.notes;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.ros.keep.commons.crud.entities.AbstractCustomEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = "notes")
public class Note extends AbstractCustomEntity<String> {

    @Id
    private String id;
    private String title;
    private String content;
    private List<Long> labelIds;
    private List<Long> imagesIds;

}
