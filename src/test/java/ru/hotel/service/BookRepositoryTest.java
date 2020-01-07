package ru.hotel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.hotel.domain.Book;
import ru.hotel.repository.BookRepository;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@DataMongoTest
@DisplayName("Тестирование монго репозитория книг")
class BookRepositoryTest {

    @BeforeEach
    void before(@Autowired BookRepository bookRepository) {
        bookRepository.save(new Book("Отзвуки серебряного ветра","Эльтеррус Иар", "Фантастика")).subscribe();
    }

    @Test
    @DisplayName("должно вернуть книгу по части названия")
    void getByTitlePart(@Autowired BookRepository bookRepository) throws InterruptedException {
        Flux<Book> book = bookRepository.findByTitleContaining("еребр");
        StepVerifier
                .create(book)
                .assertNext(b -> assertEquals(b.getTitle(),"Отзвуки серебряного ветра"));
    }

    @Test
    @DisplayName("должна быть добавлена запись без ID")
    void getByFIONew(@Autowired BookRepository bookRepository) {
        Mono<Book> book = bookRepository.save(new Book("Конституция","Народ России", "Сборник правил"));
        StepVerifier
                .create(book)
                .assertNext(b -> assertNotNull(b.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    @DisplayName("должно вернуть книгу по части ФИО автора")
    void getByAuthorPart(@Autowired BookRepository bookRepository) {
        Flux<Book> book = bookRepository.findByAuthorRegex("Иар");
        StepVerifier
                .create(book)
                .assertNext(b -> assertEquals(b.getAuthor().toString(), "[Эльтеррус Иар]"));
    }
}