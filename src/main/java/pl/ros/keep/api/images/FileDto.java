package pl.ros.keep.api.images;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.keep.api.enums.StorageLocation;
import pl.ros.keep.commons.crud.dtos.AbstractCustomDto;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto extends AbstractCustomDto<String> {
    private String id;
    private String imagePath;
    private byte[] content;
    @JsonIgnore
    private StorageLocation storageLocation;
    @JsonIgnore
    private String contentType;
    @JsonIgnore
    private Long size;
    private String filename;
}
