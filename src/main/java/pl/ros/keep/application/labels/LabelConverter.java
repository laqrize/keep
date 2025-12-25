package pl.ros.keep.application.labels;

import org.springframework.stereotype.Component;
import pl.ros.keep.api.labels.LabelDto;
import pl.ros.keep.commons.converters.IStandardRecordConverter;
import pl.ros.keep.core.jpa.labels.Label;

@Component
public class LabelConverter implements IStandardRecordConverter<LabelDto, Label> {
    @Override
    public Label toEntity(LabelDto dto) {
        Label label = Label.builder()
                .name(dto.getName())
                .build();
        setCommonFieldsToEntity(dto, label);
        return label;
    }

    @Override
    public LabelDto toDto(Label entity) {
        LabelDto labelDto = LabelDto.builder()
                .name(entity.getName())
                .build();
        setCommonFieldsToDTO(labelDto, entity);
        return labelDto;
    }
}
