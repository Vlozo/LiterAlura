package br.com.alura.literAlura.repository;

import br.com.alura.literAlura.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitleEqualsIgnoreCase(String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findBookByTitle(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE b.language ILIKE %:language%")
    List<Book> listBookByLanguage(String language);



}
