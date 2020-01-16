package ru.hotel.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.PaymentType;

public interface PaymentTypeService {
    Flux<PaymentType> getByType(String type);
    Mono<PaymentType> getById(String id);
    Mono<PaymentType> getByTypeExact(String type);
    Mono<PaymentType> savePaymentType(PaymentType paymentType);
    Flux<PaymentType> getAll();
    Mono<Void> deletePaymentType(String id);
}
