package br.com.alura.literAlura.main;

import br.com.alura.literAlura.models.Author;
import br.com.alura.literAlura.models.Book;
import br.com.alura.literAlura.models.ResponseData;
import br.com.alura.literAlura.repository.AuthorRepository;
import br.com.alura.literAlura.repository.BookRepository;
import br.com.alura.literAlura.services.ApiConnection;
import br.com.alura.literAlura.services.DataConverter;

import java.util.*;

public class Main {
    private ApiConnection connection = new ApiConnection();
    private Scanner reader = new Scanner(System.in);
    private DataConverter converter = new DataConverter();
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public Main(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public void showMenu() {
        boolean running = true;
        System.out.println("""
                -------------------------------------
                           MENU DE OPÇÕES
                -------------------------------------
                [1] Buscar e registrar um livro
                [2] Listar todos os livros registrados
                [3] Buscar livros registrados
                [4] Listar autores registrados
                [5] Listar autores vivos em determinado ano
                [6] Listar todos os livros de um autor
                [7] Listar livros por idioma
                [8] Mostrar estatísticas por idioma
                [9] Mostrar estatísticas gerais
                
                [0] Encerrar programa""");

        while (running) {
            System.out.println("-------------------------------------");
            System.out.println("Escolha uma opção: ");
            String option = reader.nextLine();

            switch (option) {
                case "0":
                    System.out.println("Execução finalizada.");
                    running = false;
                    break;
                case "1":
                    searchBook();
                    break;
                case "2":
                    listBooks();
                    break;
                case "3":
                    searchBookInRepository();
                    break;
                case "4":
                    listAuthors();
                    break;
                case "5":
                    listAuthorsAliveIn();
                    break;
                case "6":
                    listBooksFromAuthor();
                    break;
                case "7":
                    listBooksByLanguage();
                    break;
                case "8":
                    showStatsOfBooksLanguage();
                    break;
                case "9":
                    showGeneralStatistics();
                    break;
                default:
                    System.out.println("Opção inválida");
                    break;
            }
        }
    }

    private void searchBook(){
        String address = "https://gutendex.com/books/";
        System.out.println("Digite o título do livro a ser procurado:");
        var search = reader.nextLine();

        address = address + "?search=" + search.toLowerCase().replace(" ", "%20");
        String json = connection.getData(address);
        var result = converter.getData(json, ResponseData.class);

        if (!result.response().isEmpty()){
            var data = result.response().get(0);

            Book book = new Book(data);

            Author author = book.getAuthor();
            System.out.println(book);
            System.out.println("-----------------------------------");

            System.out.println("Deseja registrar esse livro no repositório?\n(digite S para confirmar)");
            var wantRegister = reader.nextLine();

            if (wantRegister.equalsIgnoreCase("s")) {
                Optional<Author> searchAuthor = authorRepository.findByNameEqualsIgnoreCase(author.getName());
                Optional<Book> searchBook = bookRepository.findByTitleEqualsIgnoreCase(book.getTitle());

                if (searchBook.isPresent()){
                    System.out.println("Atualmente este livro já está registrado no repositório.");

                } else if (searchAuthor.isPresent()){
                    Author authorFound = searchAuthor.get();
                    book.setAuthor(authorFound);
                    bookRepository.save(book);
                    System.out.println("Livro registrado com sucesso");

                } else {
                    authorRepository.save(author);
                    bookRepository.save(book);
                    System.out.println("Livro registrado com sucesso");
                }
            }

        } else {
            System.out.println("Nenhum livro foi encontrado.");
        }
    }

    private void listBooks(){
        List<Book> books = bookRepository.findAll();
        books.stream()
                .sorted(Comparator.comparing(Book::getTitle))
                .forEach(Book::showTitleAndAuthor);

    }

    private void searchBookInRepository(){
        System.out.println("Digite o nome do livro que você procura: ");
        var bookName = reader.nextLine();
        var search = bookRepository.findBookByTitle(bookName);

        if (!search.isEmpty()) {
            System.out.println(search.get(0));
        } else {
            System.out.println("Livro não encontrado.");
        }
    }

    private void listAuthors(){
        List<Author> authors = authorRepository.findAll();
        authors.stream()
                .sorted(Comparator.comparing(Author::getName))
                .forEach(System.out::println);
    }

    private void listAuthorsAliveIn(){
        while(true){
            System.out.println("Insira o Ano alvo para a listagem: ");
            String input = reader.nextLine();

            try {
                int year = Integer.parseInt(input);
                List<Author> authors = authorRepository.listAuthorsAliveIn(year);
                System.out.printf("""
                        ----------------------------------------
                            LISTANDO AUTORES VIVOS NO ANO %d
                        ----------------------------------------
                        """, year);

                if (authors.isEmpty()){
                    System.out.println("Nenhum autor foi encontrado.");
                } else {
                    authors.forEach(System.out::println);
                }
                break;
            } catch (NumberFormatException e){
                System.out.println("Entrada inválida, digite novamente. \n");

            }
        }
    }

    private void listBooksByLanguage(){
        System.out.println("""
                ----------------------------------
                        IDIOMAS FREQUENTES
                ----------------------------------
                PT - Português
                EN - Inglês
                ES - Espanhol
                FR - Francês
                ----------------------------------
                Insira o idioma dos livros a serem listados:""");

        var input = reader.nextLine();
        List<Book> books = bookRepository.listBookByLanguage(input.toLowerCase());
        if (books.isEmpty()){
            System.out.println("Nenhum livro foi encontrado para o idioma inserido.");
        } else {
            books.forEach(Book::showTitleAndAuthor);
        }
    }

    private void showStatsOfBooksLanguage(){
        System.out.println("Insira o idioma do livro para mostrar estatísticas:");
        var input = reader.nextLine();

        List<Book> books = bookRepository.listBookByLanguage(input.toLowerCase());

        if(books.isEmpty()){
            System.out.println("Nenhum livro foi encontrado.");

        } else {
            IntSummaryStatistics stats = books.stream()
                    .mapToInt(Book::getDownloads)
                    .summaryStatistics();

            System.out.printf("""
                -----------------------------------
                  Estatísticas sobre livros em %s
                -----------------------------------
                """, input.toUpperCase());
            System.out.println("Média de downloads: " + stats.getAverage());
            System.out.println("Máxima de downloads: " + stats.getMax());
            System.out.println("Mínima de downloads: " + stats.getMin());
            System.out.println("Soma de downloads: " + stats.getSum());
            System.out.println("Total de livros: " + stats.getCount());
        }

    }

    private void showGeneralStatistics(){
        List<Book> books = bookRepository.findAll();
        IntSummaryStatistics stats = books.stream()
                .mapToInt(Book::getDownloads)
                .summaryStatistics();

        System.out.println("""
                -----------------------------------
                       ESTATÍSTICAS GERAIS 
                -----------------------------------""");
        System.out.println("Média de downloads: " + stats.getAverage());
        System.out.println("Máxima de downloads: " + stats.getMax());
        System.out.println("Mínima de downloads: " + stats.getMin());
        System.out.println("Soma de downloads: " + stats.getSum());
        System.out.println("Total de livros: " + stats.getCount());
    }

    private void listBooksFromAuthor(){
        System.out.println("Digite o nome do Autor para listar seus livros: ");
        var input = reader.nextLine();
        List<Book> books = authorRepository.booksFromAuthor(input);

        if(books.isEmpty()){
            System.out.println("Não foram achados livros deste autor");
        } else {
            System.out.printf("""
                    ------------------------------------------
                        Livros de %s
                    ------------------------------------------
                    """, books.get(0).getAuthor().getNormalizedName());

            books.forEach(Book::showTitleAndDownloads);
        }
    }

}
