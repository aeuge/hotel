package ru.hotel.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Author;
import ru.hotel.domain.Book;
import ru.hotel.domain.Comment;
import ru.hotel.domain.Genre;

import java.util.List;

public interface BookService {
    Flux<Book> getByTitle(String title);
    Flux<Book> getByComment(String comment);
    Flux<Book> getByAuthor(String author);
    Flux<Book> getByGenre(String genre);
    Mono<Book> getById(String id);
    Mono<Book> saveBook(Book book);
    Flux<Book> getAll();
    Flux<Genre> getAllGenre();
    Flux<Author> getAllAuthor();
    Flux<Comment> getAllComment();
    Mono<Book> getByTitleExact(String title);
    Mono<Book> getByAuthorExact(String author);
    Mono<Void> deleteBook(String id);
}
