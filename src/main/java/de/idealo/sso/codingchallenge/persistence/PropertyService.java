package de.idealo.sso.codingchallenge.persistence;

import org.springframework.stereotype.Service;

import java.util.Optional;
import javax.inject.Inject;

@Service
public class PropertyService {
    @Inject
    PropertyRepository repository;

    public DefaultProperty getDefaultProperties() {
        Optional<DefaultProperty> properties = repository.findById(1L);
        return properties.orElseGet(()->repository.save(new DefaultProperty()));
    }

    public void setDefaultProperties(DefaultProperty properties) {
        repository.save(properties);
    }
}
