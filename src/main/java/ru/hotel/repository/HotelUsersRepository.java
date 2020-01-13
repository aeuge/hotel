package ru.hotel.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.hotel.domain.*;

@Repository
public interface HotelUsersRepository extends ReactiveMongoRepository<HotelUsers, String> {
    Mono<HotelUsers> findByUsername(String username);
}
