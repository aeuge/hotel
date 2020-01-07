package ru.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.channel.BookAnalizer;
import ru.hotel.domain.Author;
import ru.hotel.domain.Book;
import ru.hotel.domain.Comment;
import ru.hotel.domain.Genre;
import ru.hotel.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository dao;
    private BookAnalizer bookAnalizer;

    public BookServiceImpl(BookRepository dao, BookAnalizer bookAnalizer) {
        this.dao = dao;
        this.bookAnalizer = bookAnalizer;
    }

    @Override
    public Mono<Book> getByTitleExact(String title) {
        return dao.findByTitle(title);
    }

    @Override
    public Mono<Book> getByAuthorExact(String author) {
        return dao.findByAuthor(author);
    }

    @Override
    public Flux<Book> getByTitle(String title) {
        return dao.findByTitleContaining(title);
    }

    @Override
    public Flux<Book> getByComment(String comment) { return dao.findByCommentRegex(".*" + comment + ".*"); }

    @Override
    public Flux<Book> getByAuthor(String author) { return dao.findByAuthorRegex(".*" + author + ".*"); }

    @Override
    public Flux<Book> getByGenre(String genre) { return dao.findByGenreRegex(".*" + genre + ".*"); }

    @Override
    public Mono<Book> getById(String id) { return dao.findById(id); }

    @Override
    public Mono<Book> saveBook(Book book) { return dao.save(book); }

    @Override
    public Flux<Book> getAll() {
        Flux<Book> find = dao.findAll();
        List<Book> books = new ArrayList<>();
        //find.subscribe(System.out::println);
        find.subscribe(books::add);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //books.forEach(System.out::println);

        bookAnalizer.processBook(books);
        return find;
    }

    @Override
    public Flux<Genre> getAllGenre() { return dao.findAllGenre(); }

    @Override
    public Flux<Author> getAllAuthor() { return dao.findAllAuthor(); }

    @Override
    public Flux<Comment> getAllComment() { return dao.findAllComment(); }

    @Override
    public Mono<Void> deleteBook(String id) { return dao.deleteById(id); }

}
