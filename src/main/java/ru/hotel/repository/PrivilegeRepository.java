package ru.hotel.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.hotel.domain.LibraryUsers;
import ru.hotel.domain.Privilege;

@Repository
public interface PrivilegeRepository extends ReactiveMongoRepository<Privilege, String> {
    Mono<Privilege> findByName(String name);
}
