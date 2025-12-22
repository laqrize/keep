package pl.ros.keep.commons.converters;


import static pl.ros.keep.commons.utils.NpeUtils.getValue;
import pl.ros.keep.api.user.UserDto;
import pl.ros.keep.commons.crud.dtos.AbstractCustomDto;
import pl.ros.keep.commons.crud.entities.AbstractCustomEntity;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.core.jpa.users.AppUser;

public interface IStandardRecordConverter<D extends AbstractCustomDto, E extends AbstractCustomEntity> extends IConverter<D, E> {

    default void setCommonFieldsToEntity(D dto, E entity) {
        entity.setId(dto.getId());
        entity.setCreatedBy(AppUser.builder()
                .id(getValue(() -> dto.getCreatedBy().getId()))
                .email(getValue(() -> dto.getCreatedBy().getEmail()))
                .build());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setStatus(dto.getStatus());
        entity.setVersion(dto.getVersion());
    }

    default void setCommonFieldsToDTO(D dto, E entity) {
        dto.setId(entity.getId());
        dto.setCreatedBy(
                UserDto.builder()
                        .id(getValue(() -> entity.getCreatedBy().getId()))
                        .email(getValue(() -> entity.getCreatedBy().getEmail()))
                        .build()
        );
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(EntityStatus.fromCode(entity.getStatus()));
        dto.setVersion(entity.getVersion());
    }

}
