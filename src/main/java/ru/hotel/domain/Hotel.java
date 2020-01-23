package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("hotels")
public class Hotel {
    @Id
    private String id;
    private Integer kod;
    private String name;

    public Hotel() {}

    public Hotel(String name) {
        this.name = name;
    }

    public Hotel(Integer kod, String name) {
        this.kod = kod;
        this.name = name;
    }
}
