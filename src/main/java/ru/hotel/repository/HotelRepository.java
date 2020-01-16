package ru.hotel.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;

@Repository
public interface HotelRepository extends ReactiveMongoRepository<Hotel, String> {
    Flux<Hotel> findByNameContaining(String title);
    Flux<Hotel> findAll();
    Mono<Hotel> findById(String id);
    Mono<Hotel> findByName(String title);
    Mono<Void> deleteById(String id);
}
