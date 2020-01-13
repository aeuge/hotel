package ru.hotel.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import ru.hotel.domain.HotelUsers;

import java.util.List;
import java.util.stream.Collectors;

public class HotelUsersConverterService {
    public static User toUser(HotelUsers users) {
        List<SimpleGrantedAuthority> authorities = users.getRolesByString().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());;
        return new User(users.getUsername(), users.getPassword(), authorities);
    }
}
