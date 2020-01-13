package ru.hotel.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hotel.domain.HotelUsers;
import ru.hotel.domain.Privilege;
import ru.hotel.repository.HotelUsersRepository;

import java.util.Arrays;

@Service
public class MongoUserDetailsService implements ReactiveUserDetailsService {
    private HotelUsersRepository repository;

    public MongoUserDetailsService(HotelUsersRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        Mono<UserDetails> user = repository.findByUsername(username).
                switchIfEmpty(Mono.just(new HotelUsers("anonymous","anonymous",Arrays.asList(new Privilege("NONE"))))).
                map(HotelUsersConverterService::toUser);
        return user;
    }
}
