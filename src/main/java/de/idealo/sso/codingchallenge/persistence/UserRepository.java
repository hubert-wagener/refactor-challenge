package de.idealo.sso.codingchallenge.persistence;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<UserEntity, Long> {
    public UserEntity findByUserName(String userName);
}
