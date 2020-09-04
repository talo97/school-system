package com.schoolsystem.common;

import java.util.List;
import java.util.Optional;

public interface CommonService<E  extends CommonEntity> {
    E save(E entity);
    E update(E entity);
    Optional<E> get(Long id);
    List<E> getAll();
    void delete(Long id);
    void delete(E entity);

}
