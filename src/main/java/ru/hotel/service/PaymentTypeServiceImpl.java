package ru.hotel.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.domain.PaymentType;
import ru.hotel.repository.HotelRepository;
import ru.hotel.repository.PaymentTypeRepository;

@Service
public class PaymentTypeServiceImpl implements PaymentTypeService {
    private PaymentTypeRepository dao;

    public PaymentTypeServiceImpl(PaymentTypeRepository dao) { this.dao = dao; }

    @Override
    public Mono<PaymentType> getByTypeExact(String type) { return dao.findByType(type); }

    @Override
    public Flux<PaymentType> getByType(String type) {
        return dao.findByTypeContaining(type);
    }

    @Override
    public Mono<PaymentType> getById(String id) { return dao.findById(id); }

    @Override
    public Mono<PaymentType> savePaymentType(PaymentType paymentType) {
        return dao.save(paymentType);
    }

    @Override
    public Flux<PaymentType> getAll() { return dao.findAll(); }

    @Override
    public Mono<Void> deletePaymentType(String id) { return dao.deleteById(id); }

}
