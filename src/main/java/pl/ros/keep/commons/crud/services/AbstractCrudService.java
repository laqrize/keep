package pl.ros.keep.commons.crud.services;

import lombok.extern.slf4j.Slf4j;
import pl.ros.keep.commons.crud.dtos.AbstractDto;
import pl.ros.keep.commons.crud.entities.AbstractEntity;

@Slf4j
public abstract class AbstractCrudService<D extends AbstractDto, E extends AbstractEntity> extends AbstractCustomService<D,E, Long>  {
}
