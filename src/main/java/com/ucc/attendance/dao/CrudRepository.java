package com.ucc.attendance.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic abstraction for classes that save, retrieve, and delete persistent entities.
 */
public interface CrudRepository<T> {
    List<T> findAll();

    Optional<T> findById(int id);

    T save(T entity);

    void deleteById(int id);
}
