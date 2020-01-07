package ru.hotel.rest;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hotel.domain.Book;
import ru.hotel.service.BookService;

import java.security.Principal;

@RestController
public class RestBookController {

    private final BookService service;

    @Autowired
    public RestBookController(BookService service) {
        this.service = service;
    }

    @GetMapping("/api/allbooks")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'read')")
    @HystrixCommand(fallbackMethod = "getDefaultBooks", groupKey = "BookService", commandKey = "findAll", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500")})
    public Flux<BookDto> getAllBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) throws InterruptedException {
        System.out.println("Privileges: " + ((Authentication) principal).getAuthorities());
        Thread.sleep(10000);
        return service.getAll().map(ConverterBookToDto::toDto);
    }

    @GetMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "BookService", commandKey = "findBook")
    public Mono<BookDto> getBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(id).map(ConverterBookToDto::toDto);
    }

    @DeleteMapping("/book/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @HystrixCommand(groupKey = "BookService", commandKey = "deleteBook")
    public Mono<Void> deleteBook(@PathVariable String id, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.deleteBook(id);
    }

    @PostMapping("/api/book/{id}")
    @PreAuthorize("@reactivePermissionEvaluator.hasPermission(#principal, 'book', 'write')")
    @HystrixCommand(groupKey = "BookService", commandKey = "saveBook")
    public Mono<Book> saveBook(@RequestBody BookDto bookDto, @AuthenticationPrincipal(expression = "principal") Principal principal) {
        return service.getById(bookDto.getId()).switchIfEmpty(Mono.just(new Book()))
                .map(v -> {
                    v.setTitle(bookDto.getTitle());
                    v.setAuthor(bookDto.getAuthor());
                    v.setGenre(bookDto.getGenre());
                    v.setComment(bookDto.getComment());
                    return v;
                }).flatMap(service::saveBook);
    }

    public Flux<BookDto> getDefaultBooks(@AuthenticationPrincipal(expression = "principal") Principal principal) {
        return Flux.just(new BookDto("Конституция"));
    }
}