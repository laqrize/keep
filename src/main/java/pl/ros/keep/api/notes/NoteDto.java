package pl.ros.keep.api.notes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import pl.ros.keep.api.images.ImageDto;
import pl.ros.keep.api.labels.LabelDto;
import pl.ros.keep.commons.crud.dtos.AbstractCustomDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class NoteDto extends AbstractCustomDto<String> {
    private String id;
    private String title;
    private String content;
    private List<LabelDto> labels;
    private List<ImageDto> images;

    public List<Long> getLabelIds() {
        if (labels == null) {
            return new ArrayList<>();
        }
        return labels.stream()
                .map(LabelDto::getId)
                .toList();
    }

    public void setLabelIds(List<Long> labelIds) {
        if (labelIds == null) {
            this.labels = new ArrayList<>();
            return;
        }
        this.labels = (List<LabelDto>) labelIds.stream()
                .map(id -> LabelDto.builder().id(id).build())
                .toList();
    }


    public List<Long> getImagesIds() {
        if (images == null) {
            return new ArrayList<>();
        }
        return images.stream()
                .map(ImageDto::getId)
                .toList();
    }

    public void setImagesIds(List<Long> imagesIds) {
        if (imagesIds == null) {
            this.images = new ArrayList<>();
            return;
        }
        this.images = (List<ImageDto>) imagesIds.stream()
                .map(id -> ImageDto.builder().id(id).build())
                .toList();
    }

}