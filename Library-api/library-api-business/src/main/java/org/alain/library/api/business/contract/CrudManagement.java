package org.alain.library.api.business.contract;

import java.util.List;
import java.util.Optional;

public interface CrudManagement<T> {
    Optional<T> findOne(Long id);
    List<T> findAll();
    void delete(Long id);
    T save(T obj);
}
