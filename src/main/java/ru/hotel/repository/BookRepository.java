package ru.hotel.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Author;
import ru.hotel.domain.Book;
import ru.hotel.domain.Comment;
import ru.hotel.domain.Genre;

@Repository
public interface BookRepository extends ReactiveMongoRepository<Book, String> {
    Flux<Book> findByTitleContaining(String title);
    Flux<Book> findByCommentRegex(String comment);
    Flux<Book> findByAuthorRegex(String author);
    Flux<Book> findByGenreRegex(String genre);
    Flux<Book> findAll();
    @Query("{},{genre:1}")
    Flux<Genre> findAllGenre();
    @Query("{},{author:1}")
    Flux<Author> findAllAuthor();
    @Query("{},{comment:1}")
    Flux<Comment> findAllComment();
    Mono<Book> findById(String id);
    Mono<Book> findByTitle(String title);
    Mono<Book> findByAuthor(String author);
    Mono<Void> deleteById(String id);
}
