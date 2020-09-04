package com.schoolsystem.common;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class CommonServiceImpl<E extends CommonEntity, R extends JpaRepository<E, Long>> implements CommonService<E> {

    protected final R repository;

    public CommonServiceImpl(R repository) {
        this.repository = repository;
    }

    @Override
    public final E save(E entity) {
        return repository.save(entity);
    }

    @Override
    public final E update(E entity) {
        return repository.saveAndFlush(entity);
    }

    @Override
    public final Optional<E> get(Long id) {
        return repository.findById(id);
    }

    @Override
    public final List<E> getAll() {
        return repository.findAll();
    }

    @Override
    public final void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public final void delete(E entityGroup) {
        repository.delete(entityGroup);
    }
}
