package ru.hotel.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Data
@Document("books")
public class Book {
    @Id
    private String id;
    private String title;
    private List<String> author = new ArrayList<>();
    private List<String> genre = new ArrayList<>();
    private List<String> comment = new ArrayList<>();

    public Book () {}

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author.add(author);
        this.genre.add(genre);
    }

    public Book(String title) {
        this.title = title;
    }

    public Book(String id, String title, String author, String genre, String comment) {
        this.id = id;
        this.title = title;
        this.author.add(author);
        this.genre.add(genre);
        this.comment.add(comment);
    }
}
