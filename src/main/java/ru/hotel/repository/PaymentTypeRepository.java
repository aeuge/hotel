package ru.hotel.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.PaymentType;

@Repository
public interface PaymentTypeRepository extends ReactiveMongoRepository<PaymentType, String> {
    Flux<PaymentType> findByTypeContaining(String type);
    Flux<PaymentType> findAll();
    Mono<PaymentType> findById(String id);
    Mono<PaymentType> findByType(String type);
    Mono<Void> deleteById(String id);
}
