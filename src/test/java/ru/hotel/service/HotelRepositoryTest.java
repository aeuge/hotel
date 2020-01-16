package ru.hotel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.hotel.domain.Hotel;
import ru.hotel.repository.HotelRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataMongoTest
@DisplayName("Тестирование монго репозитория отелей")
class HotelRepositoryTest {

    @BeforeEach
    void before(@Autowired HotelRepository hotelRepository) {
        hotelRepository.save(new Hotel("1111","Тестовый отель")).subscribe();
    }

    @Test
    @DisplayName("должно вернуть отель по части названия")
    void getByTitlePart(@Autowired HotelRepository hotelRepository) throws InterruptedException {
        Flux<Hotel> book = hotelRepository.findByNameContaining("отель");
        StepVerifier
                .create(book)
                .assertNext(b -> assertEquals(b.getName(),"Тестовый отель"));
    }

    @Test
    @DisplayName("должна быть добавлена запись без ID")
    void getByFIONew(@Autowired HotelRepository hotelRepository) {
        Mono<Hotel> book = hotelRepository.save(new Hotel("1111","Тестовый отель"));
        StepVerifier
                .create(book)
                .assertNext(b -> assertNotNull(b.getId()))
                .expectComplete()
                .verify();
    }

}