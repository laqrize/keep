package pl.ros.keep.api.labels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.keep.commons.crud.dtos.AbstractDto;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LabelDto extends AbstractDto {
    private Long id;
    private String name;
}
