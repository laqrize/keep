package pl.ros.keep.commons.converters;

import org.springframework.data.domain.Page;

import java.util.List;

public interface IConverter<D extends Object, E extends Object>{

    E toEntity(D dto);
    D toDto(E entity);

    default List<E> toEntityList(List<D> dto){
        return dto.stream().map(this::toEntity).toList();
    }
    default List<D> toDtoList(List<E> entities){
        return entities.stream().map(this::toDto).toList();
    }
    default Page<D> toDtoPage(Page<E> entityPage){
        return entityPage.map(this::toDto);
    }

}
