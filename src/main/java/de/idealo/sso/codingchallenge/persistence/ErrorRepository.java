package de.idealo.sso.codingchallenge.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ErrorRepository extends CrudRepository<ErrorEntity, Long> {
}
