package com.marco.springdatajpasandbox.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void testFindByName(){
        entityManager.persist(new Book("C++"));

        List<Book> books = bookRepository.findByName("C++");

        assertEquals(1, books.size());
        assertThat(books.stream()
                .allMatch(book -> book.getName().equals("C++")), is(true));

    }

    @Test
    public void testFindByNameMatching(){
        final Book persisted = entityManager.persist(new Book("C++"));
        Long generatedId = persisted.getId();

        Book book = bookRepository.findByIdAndName(generatedId,"C++");

        assertThat(book, notNullValue());

    }

    @Test
    public void testFindByNameNonMatching(){
        entityManager.persist(new Book("C++"));
        final Long anyRandomId = new Random().nextLong();
        final String anyRandomName = "CUALQUIERCOSA";

        Book book = bookRepository.findByIdAndName(anyRandomId,anyRandomName);

        assertThat(book, nullValue());

    }

    @Test
    public void testFindOptionalByNameMatching(){
        final Book persisted = entityManager.persist(new Book("C++"));
        Long generatedId = persisted.getId();

        final Optional<Book> optionalBook = bookRepository.findOptionalByIdAndName(generatedId, "C++");

        assertThat(optionalBook.isPresent(), is(true));

    }

    @Test
    public void testFindOptionalByNameNonMatching(){
        entityManager.persist(new Book("C++"));
        final Long anyRandomId = new Random().nextLong();
        final String anyRandomName = "CUALQUIERCOSA";

        final Optional<Book> optionalBook = bookRepository.findOptionalByIdAndName(anyRandomId, anyRandomName);

        assertThat(optionalBook.isPresent(), is(false));

    }

    @Test
    public void testFindByISBNMatching(){
        final Book entity = new Book("C++");
        entity.setIsbn("123456");
        entityManager.persist(entity);

        final Optional<Book> optionalBook = bookRepository.findByIsbn("123456");

        assertThat(optionalBook.isPresent(), is(true));

    }

    @Test
    public void testFindAllByName_as_Stream() {
        final Book entity1 = new Book("prueba");
        final Book entity2 = new Book("prueba");
        final Book entity3 = new Book("prueba");
        entityManager.persist(entity1);
        entityManager.persist(entity2);
        entityManager.persist(entity3);

        final long count = bookRepository.findAllByName("prueba")
                .filter(book -> book.getName().equals("prueba"))
                .count();

        assertThat((int) count, is(3));

    }

    @Test
    public void testFindAllSorted() {
        IntStream.of(10)
                .forEach(i -> entityManager.persist(new Book("Libro" + i)));

        Sort sort = new Sort(Sort.Direction.ASC, "name");
        final Book firstBook = bookRepository.findAll(sort).stream()
                .findFirst().get();

        assertThat(firstBook.getName(), equalTo("Libro10"));
    }

    @Test
    public void testAsync() throws ExecutionException, InterruptedException, TimeoutException {
        IntStream.rangeClosed(1,10)
                .forEach(i -> entityManager.persist(new Book("Libro" + i)));

        final CompletableFuture<List<Book>> completableFuture = bookRepository.findAllAsync();

        final List<Book> books = completableFuture.get(5, TimeUnit.SECONDS);
        System.out.println(books.size());
        System.out.println("waiting...");
        Thread.sleep(6*1000);

        System.out.println(books.size());

    }

    @Test
    public void testFindByAuthorName() {
        Person author1 = new Person("Juan");
        entityManager.persist(author1);
        IntStream.rangeClosed(1,5)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author1.getName(), author1));
                });
        Person author2 = new Person("Pedro");
        entityManager.persist(author2);
        IntStream.rangeClosed(1,10)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author2.getName(), author2));
                });

        final List<Book> books = bookRepository.findByAuthor_Name("Juan");

        assertThat(books.size(), equalTo(5));
    }

    @Test
    public void testFindByCustomParam() {
        Person author = new Person("Tolkien");
        entityManager.persist(author);
        entityManager.persist(new Book("El hobbit", author));

        final Optional<Book> optionalBook = bookRepository.findByCustomParamName("El hobbit");

        assertThat(optionalBook.isPresent(), is(true));
        assertThat(optionalBook.get().getName(), equalTo("El hobbit"));
    }

    @Test
    public void testDeleteByName() {
        Person author = new Person("Tolkien");
        entityManager.persist(author);
        entityManager.persist(new Book("El hobbit", author));

        bookRepository.deleteByName("El hobbit");

        final List<Book> books = bookRepository.findByName("El hobbit");
        assertThat(books, empty());

    }

    @Test
    public void testFindNamesByAuthor() {
        Person author1 = new Person("Juan");
        entityManager.persist(author1);
        IntStream.rangeClosed(1,5)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author1.getName(), author1));
                });
        Person author2 = new Person("Pedro");
        entityManager.persist(author2);
        IntStream.rangeClosed(1,10)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author2.getName(), author2));
                });

        //ojo que no devuelve book sino NamesOnly, porque es compatible con book
        final Collection<NamesOnly> namesOnly = bookRepository.findNamesByAuthor_Name("Pedro");

        assertThat(namesOnly.size(), equalTo(10));

    }

    @Test
    public void testFindDynamicProyectionByAuthorName() {
        Person author1 = new Person("Juan");
        entityManager.persist(author1);
        IntStream.rangeClosed(1,5)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author1.getName(), author1));
                });
        Person author2 = new Person("Pedro");
        entityManager.persist(author2);
        IntStream.rangeClosed(1,10)
                .forEach(i -> {
                    entityManager.persist(new Book("Libro" + i + " de " + author2.getName(), author2));
                });

        final Collection<NamesOnly> namesOnly = bookRepository.findByAuthor_Name("Pedro", NamesOnly.class);

        namesOnly.stream()
                .forEach(n -> System.out.println(n.getNameAndAuthor()));

        assertThat(namesOnly.size(), equalTo(10));
        assertThat(namesOnly.stream()
                .allMatch(n -> n.getNameAndAuthor().endsWith("(Pedro)")), is(true));

    }

}