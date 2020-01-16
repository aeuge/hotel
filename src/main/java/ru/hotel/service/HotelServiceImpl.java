package ru.hotel.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;
import ru.hotel.repository.HotelRepository;

@Service
public class HotelServiceImpl implements HotelService {
    private HotelRepository dao;

    public HotelServiceImpl(HotelRepository dao) { this.dao = dao; }

    @Override
    public Mono<Hotel> getByNameExact(String name) { return dao.findByName(name); }

    @Override
    public Flux<Hotel> getByName(String name) {
        return dao.findByNameContaining(name);
    }

    @Override
    public Mono<Hotel> getById(String id) { return dao.findById(id); }

    @Override
    public Mono<Hotel> saveHotel(Hotel hotel) { return dao.save(hotel); }

    @Override
    public Flux<Hotel> getAll() { return dao.findAll(); }

    @Override
    public Mono<Void> deleteHotel(String id) { return dao.deleteById(id); }

}
