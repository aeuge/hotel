package ru.hotel.rest;

import ru.hotel.domain.Hotel;

public class ConverterBookToDto {
    public static BookDto toDto(Hotel hotel) {
        BookDto bookDto = new BookDto(hotel.getName());
        bookDto.setId(hotel.getId());
        bookDto.setAuthor(hotel.getAuthor());
        bookDto.setGenre(hotel.getGenre());
        bookDto.setComment(hotel.getComment());
        return bookDto;
    }
}
