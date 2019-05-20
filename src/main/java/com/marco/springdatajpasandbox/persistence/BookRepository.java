package com.marco.springdatajpasandbox.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface BookRepository extends CrudRepository<Book, Long> {

    List<Book> findByName(String name);

    Book findByIdAndName(Long id, String name);

    Optional<Book> findOptionalByIdAndName(Long id, String name);

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where name = :name")
    Stream<Book> findAllByName(String name);

    List<Book> findAll(Sort sort);

    @Async
    @Query("select b from Book b")
    CompletableFuture<List<Book>> findAllAsync();

    List<Book> findByAuthor_Name(String author_name);

    @Query("select b from Book b where name = :customParamName")
    Optional<Book> findByCustomParamName(@Param("customParamName") String string);

    @Modifying
    @Query("delete from Book b where b.name = :name")
    void deleteByName(String name);

    Collection<NamesOnly> findNamesByAuthor_Name(String name);

    <T> Collection<T> findByAuthor_Name(String author_name, Class<T> type);
}
