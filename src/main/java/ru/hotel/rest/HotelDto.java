package ru.hotel.rest;

import lombok.Data;
import ru.hotel.domain.Hotel;

import static ru.hotel.rest.ConverterHotelToDto.toDto;

@Data
public class HotelDto {
    private String id;
    private String kod;
    private String name;

    public HotelDto() { }

    public HotelDto(String name) {
        this.name = name;
        this.kod = "";
        this.id = "";
    }

    public HotelDto(Hotel hotel) { toDto(hotel); }
}
