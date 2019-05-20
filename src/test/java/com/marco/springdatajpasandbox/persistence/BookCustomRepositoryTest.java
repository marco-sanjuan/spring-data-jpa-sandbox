package com.marco.springdatajpasandbox.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookCustomRepositoryTest {


    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookCustomRepository bookRepository;

    @Test
    public void testFindByName(){
        entityManager.persist(new Book("C++"));

        List<Book> books = bookRepository.findByName("C++");

        assertEquals(1, books.size());
        assertThat(books.stream()
                .allMatch(book -> book.getName().equals("C++")), is(true));

    }


}