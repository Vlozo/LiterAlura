package br.com.alura.literAlura.models;

import jakarta.persistence.*;

@Entity
@Table(name="Books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne
    private Author author;

    private String language;

    private int downloads;

    public Book() {
    }

    public Book(BookData bookData) {
        this.title = bookData.title();
        try {
            this.author = new Author(bookData.author().get(0));
        } catch (IndexOutOfBoundsException e) {
            this.author = new Author("Desconhecido");
        }

        this.language = bookData.languages().get(0);
        this.downloads = bookData.downloads();
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public String getLanguage() {
        return language;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void showTitleAndAuthor(){
        System.out.println("Livro: " + title + " | Autor: " + author.getNormalizedName() + " | [Downloads: " + downloads + "] | [" + language + "]");
    }

    public void showTitleAndDownloads(){
        System.out.println("Livro: " + title + " [Downloads: " + downloads + "]");
    }

    @Override
    public String toString() {
        return "Livro: " + title +
                ", "+ author +
                ", Idioma: " + language +
                ", Downloads: " + downloads;
    }
}
