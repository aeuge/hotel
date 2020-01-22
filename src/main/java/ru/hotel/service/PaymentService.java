package ru.hotel.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.Payment;

import java.time.LocalDate;

public interface PaymentService {
    Mono<Payment> getById(String id);
    Mono<Payment> getByKodHotelAndData(Integer kodHotel, LocalDate data);
    Mono<Payment> savePayment(Payment payment);
    Flux<Payment> getAll();
    Mono<Void> deletePayment(String id);
}
