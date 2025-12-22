package pl.ros.keep.api.notes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.keep.commons.crud.dtos.AbstractCustomDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NoteDto extends AbstractCustomDto<String> {
    private String id;
    private String title;
    private String content;
    // TODO images and labels
}
