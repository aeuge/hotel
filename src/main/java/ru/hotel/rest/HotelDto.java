package ru.hotel.rest;

import lombok.Data;
import ru.hotel.domain.Hotel;

import java.time.LocalDate;

import static ru.hotel.rest.ConverterHotelToDto.toDto;

@Data
public class HotelDto {
    private String id;
    private Integer kod;
    private String name;

    public HotelDto() { }

    public HotelDto(String name) {
        this.name = name;
        this.kod = 0;
        this.id = "";
    }

    public HotelDto(Hotel hotel) { toDto(hotel); }
}
