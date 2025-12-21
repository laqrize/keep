package pl.ros.keep.application.users.services;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.ros.keep.api.user.UserDto;
import pl.ros.keep.application.auth.services.ContextService;
import pl.ros.keep.commons.crud.enums.CrudOperation;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.crud.services.AbstractCrudService;
import pl.ros.keep.commons.utils.NpeUtils;
import pl.ros.keep.core.users.AppUser;
import pl.ros.keep.core.users.UserRepository;
import pl.ros.keep.infrastracture.exceptions.ConflictException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractCrudService<UserDto, AppUser> {
    private final UserRepository userRepository;
    @Value("${system.user.email}")
    private String systemUserEmail;
    private final ContextService contextService;
    public UserDetailsService userDetailsService() {
        return this::findEntityByEmail;
    }

    @Override
    @Transactional
    public void delete(@NonNull Long id) {
        AppUser user = findById(id);
        user.setEnabled(false);
        repository.save(user);
        super.delete(id);
    }

    public UserDto findByEmail(@NonNull String email) {
        return converter.toDto(findEntityByEmail(email));
    }

    public AppUser findEntityByEmail(@NonNull String email) {
        return userRepository.findActualByEmail(email).orElseThrow(() -> new IllegalArgumentException(String.format("User with email %s not found", email)));
    }

    @Override
    protected void setEntityFields(AppUser entity, UserDto dto) {
        entity.setType(NpeUtils.getName(dto.getType()));
        entity.setLocked(dto.getLocked() != null && dto.getLocked());
    }

    @Override
    protected void validate(UserDto dto, @NonNull CrudOperation mode) {
        super.validate(dto, mode);
        List<AppUser> optUsers = userRepository.findActualByEmailOrUsername(dto.getEmail(), dto.getUsername());
        if(optUsers.stream().anyMatch(a -> !a.getId().equals(dto.getId()))) {
            throw new ConflictException("User with email already exists");
        }

        if (mode == CrudOperation.CREATE) {
            Assert.notNull(dto.getPasswordHash(), "Password cannot be null");
        }
    }

    public List<UserDto> findAllActive() {
        return converter.toDtoList(userRepository.findAllByStatusIn(Arrays.asList(EntityStatus.CURRENT.getCode())))
                .stream().filter(userDto -> !Arrays.asList(systemUserEmail, contextService.getCurrentUser().getEmail()).contains(userDto.getEmail())).collect(Collectors.toList());
    }

}