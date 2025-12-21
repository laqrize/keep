package pl.ros.keep.application.auth.converters;

import org.springframework.stereotype.Component;
import pl.ros.keep.api.enums.UserType;
import pl.ros.keep.api.user.UserDto;
import pl.ros.keep.commons.converters.IStandardRecordConverter;
import pl.ros.keep.commons.utils.NpeUtils;
import pl.ros.keep.core.users.AppUser;

@Component
public class UserConverter implements IStandardRecordConverter<UserDto, AppUser> {
    @Override
    public AppUser toEntity(UserDto dto) {
        AppUser user = AppUser.builder()
                .id(dto.getId())
                .type(NpeUtils.getName(dto.getType()))
                .locked(dto.getLocked())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .passwordHash(dto.getPasswordHash())
                .enabled(dto.getEnabled())
                .build();
        setCommonFieldsToEntity(dto, user);
        return user;
    }

    @Override
    public UserDto toDto(AppUser entity) {
        UserDto dto =  UserDto.builder()
                .id(entity.getId())
                .type(NpeUtils.getValue(entity.getType(), UserType.class))
                .locked(entity.getLocked())
                .enabled(entity.getEnabled())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .passwordHash(null) // password is not needed in dto
                .build();
        setCommonFieldsToDTO(dto, entity);
        return dto;
    }

}
