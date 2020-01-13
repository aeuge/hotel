package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.hotel.service.MongoPrivilegeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Document("users")
public class HotelUsers {
        @Id
        public String id;

        public String username;
        public String password;
        @DBRef
        public List<Privilege> roles = new ArrayList<>();

        public HotelUsers(String username, String password, List<Privilege> roles) {
                this.username = username;
                this.password = password;
                this.roles.addAll(roles);
        }

        public List<String> getRolesByString () {
                return roles.stream().map(Privilege::getName).collect(Collectors.toList());
        }
}
