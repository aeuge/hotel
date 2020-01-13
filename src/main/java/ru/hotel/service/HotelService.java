package ru.hotel.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Hotel;

public interface HotelService {
    Flux<Hotel> getByName(String name);
    Mono<Hotel> getById(String id);
    Mono<Hotel> saveBook(Hotel hotel);
    Flux<Hotel> getAll();
    Mono<Hotel> getByNameExact(String name);
    Mono<Void> deleteHotel(String id);
}
