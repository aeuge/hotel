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
@DisplayName("Тестирование монго репозитория книг")
class HotelRepositoryTest {

    @BeforeEach
    void before(@Autowired HotelRepository hotelRepository) {
        hotelRepository.save(new Hotel("Отзвуки серебряного ветра","Эльтеррус Иар", "Фантастика")).subscribe();
    }

    @Test
    @DisplayName("должно вернуть книгу по части названия")
    void getByTitlePart(@Autowired HotelRepository hotelRepository) throws InterruptedException {
        Flux<Hotel> book = hotelRepository.findByTitleContaining("еребр");
        StepVerifier
                .create(book)
                .assertNext(b -> assertEquals(b.getName(),"Отзвуки серебряного ветра"));
    }

    @Test
    @DisplayName("должна быть добавлена запись без ID")
    void getByFIONew(@Autowired HotelRepository hotelRepository) {
        Mono<Hotel> book = hotelRepository.save(new Hotel("Конституция","Народ России", "Сборник правил"));
        StepVerifier
                .create(book)
                .assertNext(b -> assertNotNull(b.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("должно вернуть книгу по части ФИО автора")
    void getByAuthorPart(@Autowired HotelRepository hotelRepository) {
        Flux<Hotel> book = hotelRepository.findByAuthorRegex("Иар");
        StepVerifier
                .create(book)
                .assertNext(b -> assertEquals(b.getAuthor().toString(), "[Эльтеррус Иар]"));
    }
}