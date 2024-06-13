package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.models.Author;
import br.com.alura.literAlura.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByNameEqualsIgnoreCase(String name);

    @Query("SELECT a FROM Author a WHERE a.birthYear < :year AND a.deathYear > :year")
    List<Author> listAuthorsAliveIn(int year);

    @Query("SELECT b FROM Author a JOIN a.books b WHERE a.name ILIKE %:authorName%")
    List<Book> booksFromAuthor(String authorName);
}
