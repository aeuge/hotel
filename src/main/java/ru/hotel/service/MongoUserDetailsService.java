package ru.hotel.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hotel.domain.LibraryUsers;
import ru.hotel.domain.Privilege;
import ru.hotel.repository.LibraryUsersRepository;

import java.util.Arrays;

@Service
public class MongoUserDetailsService implements ReactiveUserDetailsService {
    private LibraryUsersRepository repository;

    public MongoUserDetailsService(LibraryUsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<UserDetails> user = repository.findByUsername(username).
                switchIfEmpty(Mono.just(new LibraryUsers("anonymous","anonymous",Arrays.asList(new Privilege("NONE"))))).
                map(LibraryUsersConverterService::toUser);
        return user;
    }
}
