package br.com.alura.literAlura.models;

import jakarta.persistence.*;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "Authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;

    private int birthYear;

    private int deathYear;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Book> books = new ArrayList<>();

    public Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public Author(AuthorData authorData) {
        this.name = authorData.name();
        this.birthYear = Optional.ofNullable(authorData.birthYear()).orElse(0);
        this.deathYear = Optional.ofNullable(authorData.deathYear()).orElse(0);
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        try {
            String[] parts = getName().split(", ");
            return parts[1] + " " + parts[0];
        } catch (Exception e) {
            return getName();
        }
    }

    @Override
    public String toString() {
        return "Autor: " + name +
                ", Ano do Nascimento: " + birthYear +
                ", Ano do Falecimento: " + deathYear;
    }
}
