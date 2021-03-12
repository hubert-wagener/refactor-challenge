package de.idealo.sso.codingchallenge.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryRepository extends CrudRepository<HistoryEntity, Long> {
    List<HistoryEntity> findFirst10ByOrderByDateCreateDesc();
}
