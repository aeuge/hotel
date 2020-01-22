package ru.hotel.repository;

import org.springframework.boot.actuate.integration.IntegrationGraphEndpoint;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.Payment;

import java.time.LocalDate;

@Repository
public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {
    Flux<Payment> findAll();
    Mono<Payment> findById(String id);
    Mono<Payment> findByKodHotelAndData(Integer kodHotel, LocalDate data);
    Mono<Void> deleteById(String id);
}
