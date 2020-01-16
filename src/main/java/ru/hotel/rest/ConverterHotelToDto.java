package ru.hotel.rest;

import ru.hotel.domain.Hotel;

public class ConverterHotelToDto {
    public static HotelDto toDto(Hotel hotel) {
        HotelDto hotelDto = new HotelDto(hotel.getName());
        hotelDto.setId(hotel.getId());
        hotelDto.setKod(hotel.getKod());
        return hotelDto;
    }
}
