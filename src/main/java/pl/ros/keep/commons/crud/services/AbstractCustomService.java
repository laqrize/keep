package pl.ros.keep.commons.crud.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import pl.ros.keep.application.auth.services.ContextService;
import pl.ros.keep.commons.crud.dtos.AbstractCustomDto;
import pl.ros.keep.commons.crud.entities.AbstractCustomEntity;
import pl.ros.keep.commons.crud.enums.CrudOperation;
import pl.ros.keep.commons.crud.enums.EntityStatus;
import pl.ros.keep.commons.converters.IStandardRecordConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public abstract class AbstractCustomService<D extends AbstractCustomDto, E extends AbstractCustomEntity, ID> {

    @Autowired
    protected CrudRepository<E, ID> repository;

    @Autowired
    protected IStandardRecordConverter<D, E> converter;

    @Autowired
    protected ContextService contextService;

    public D create(D dto) {
        validate(dto, CrudOperation.CREATE);
        E entity = converter.toEntity(dto);
        entity.updateState(contextService.getCurrentUserId(), CrudOperation.CREATE);
        entity = repository.save(entity);
        return converter.toDto(entity);
    }

    public D update(@NonNull ID id, D dto) {
        Assert.isTrue(id.equals(dto.getId()), "Id in path and body must be the same");
        validate(dto, CrudOperation.UPDATE);
        E entity = findById((ID) dto.getId());
        setEntityFields(entity, dto);
        entity.updateState(contextService.getCurrentUserId(), CrudOperation.UPDATE);
        entity = repository.save(entity);
        return converter.toDto(entity);
    }

    public void delete(@NonNull ID id) {
        E entity = findById(id);
        entity.setStatus(EntityStatus.DELETED);
        entity.updateState(contextService.getCurrentUserId(), CrudOperation.DELETE);
        repository.save(entity);
    }

    public E findById(@NonNull ID id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Entity ({}) with id {} not found", getEntityClass().getSimpleName(), id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });
    }

    public D findDtoById(@NonNull ID id) {
        return converter.toDto(findById(id));
    }
//
//    public Page<D> paged(FilteredPageRequest<D> filter) {
//        if (repository instanceof JpaSpecificationExecutor filterRepository) {
//            FilterPageObject<E> filterPageObject = filter.getFilterPageObject(
//                    getValue(() -> converter.toEntity(filter.getData())), getEntityClass());
//            Specification<E> spec = filterPageObject.byFilterCriteria(filterPageObject);
//            Pageable pageable = filterPageObject.toPageRequest();
//            log.trace("Applying filter: {}", spec);
//            log.trace("Applying page: {}", pageable);
//            return converter.toDtoPage(filterRepository.findAll(spec, pageable));
//        }
//        throw new IllegalArgumentException("Repository does not support filtering");
//    }

    protected void validate(D dto, CrudOperation mode) {
        Assert.notNull(dto, "Object cannot be null");
        if (mode == CrudOperation.CREATE) {
            Assert.isNull(dto.getId(), "Id must be null");
        } else {
            Assert.notNull(dto.getId(), "Id cannot be null");
        }
    }

    protected Class<E> getEntityClass() {
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] arguments = parameterizedType.getActualTypeArguments();
            return (Class<E>) arguments[1];
        }
        throw new IllegalArgumentException("Cannot determine entity class");
    }
    protected abstract void setEntityFields(E entity, D dto);

}
