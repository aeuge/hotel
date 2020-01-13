package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("hotels")
public class Hotel {
    @Id
    private String id;
    private String kod;
    private String name;

    public Hotel() {}

    public Hotel(String name) {
        this.name = name;
    }

}
