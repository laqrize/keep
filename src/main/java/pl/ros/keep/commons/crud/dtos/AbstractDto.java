package pl.ros.keep.commons.crud.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractDto extends AbstractCustomDto<Long>{

}
