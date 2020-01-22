package ru.hotel.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.Payment;
import ru.hotel.repository.HotelRepository;
import ru.hotel.repository.PaymentRepository;

import java.time.LocalDate;

@Service
public class PaymentServiceImpl implements PaymentService {
    private PaymentRepository dao;

    public PaymentServiceImpl(PaymentRepository dao) { this.dao = dao; }

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

}
