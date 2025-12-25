package pl.ros.keep.application.labels;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.ros.keep.api.labels.LabelDto;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCrudService;
import pl.ros.keep.core.jpa.labels.Label;
import pl.ros.keep.core.jpa.labels.LabelRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelsService extends AbstractCrudService<LabelDto, Label> {

    @Override
    protected void setEntityFields(Label entity, LabelDto dto) {
        entity.setName(dto.getName());
    }

    public List<LabelDto> getAll() {
        return converter.toDtoList(getRepository().findAllByCreatedByAndStatus(
                contextService.getCurrentUser(),
                EntityStatus.CURRENT.getCode()
        ));
    }

    public List<LabelDto> findByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return converter.toDtoList(getRepository().findAllByIdInAndStatus(ids, EntityStatus.CURRENT.getCode()));
    }

    private LabelRepository getRepository() {
        return (LabelRepository) repository;
    }
}
