package ru.hotel.service;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Payment;
import ru.hotel.rest.PaymentDto;
import ru.hotel.repository.PaymentRepository;

import java.time.LocalDate;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository dao;
    private ReactiveMongoTemplate reactiveMongoTemplate;

    public PaymentServiceImpl(PaymentRepository dao, ReactiveMongoTemplate reactiveMongoTemplate) {
        this.dao = dao;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Payment> getById(String id) { return dao.findById(id); }

    @Override
    public Mono<Payment> getByKodHotelAndData(Integer kodHotel, LocalDate data) {
        return dao.findByKodHotelAndData(kodHotel, data);
    }

    @Override
    public Mono<Payment> savePayment(Payment payment) {
        return dao.findByKodHotelAndData(payment.getKodHotel(), payment.getData()).switchIfEmpty(Mono.just(new Payment()))
                .map(v -> {
                    v.setAll(payment);
                    return v;
                }).flatMap(dao::save);
    }

    @Override
    public Flux<Payment> getAll() { return dao.findAll(); }

    @Override
    public Mono<Void> deletePayment(String id) { return dao.deleteById(id); }

    public Flux<PaymentDto> agreg(LocalDate beginDate, LocalDate endDate) {
        Aggregation agg = newAggregation(
                Payment.class,
                match(where("data").gte(beginDate).lte(endDate)),
                group("kodHotel")
                        .addToSet("kodHotel").as("kodHotel")
                        .sum("paymentTotal").as("paymentTotal")
                        .sum("payment1").as("payment1")
                        .sum("payment2").as("payment2")
                        .sum("payment3").as("payment3")

        );
        Flux<PaymentDto> ar = reactiveMongoTemplate.aggregate(agg,"payments", PaymentDto.class);
        return ar;
    }

}
