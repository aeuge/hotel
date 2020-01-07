package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("privilege")
public class Privilege {
    @Id
    public String id;

    public String name;

    public Privilege(String name) {
        this.name = name;
    }

    public Privilege(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Privilege() {
    }
}
