package com.hotel.service;

import java.util.List;
import java.util.Optional;

public interface Service<T, ID> {
    T create(T entity);
    Optional<T> getById(ID id);
    List<T> getAll();
    T update(T entity);
    void delete(ID id);
}
