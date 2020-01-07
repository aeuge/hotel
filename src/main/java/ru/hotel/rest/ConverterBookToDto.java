package ru.hotel.rest;

import ru.hotel.domain.Book;

public class ConverterBookToDto {
    public static BookDto toDto(Book book) {
        BookDto bookDto = new BookDto(book.getTitle());
        bookDto.setId(book.getId());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setGenre(book.getGenre());
        bookDto.setComment(book.getComment());
        return bookDto;
    }
}
