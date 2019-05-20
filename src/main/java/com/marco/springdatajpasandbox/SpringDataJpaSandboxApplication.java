package com.marco.springdatajpasandbox;

import com.marco.springdatajpasandbox.persistence.Book;
import com.marco.springdatajpasandbox.persistence.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataJpaSandboxApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SpringDataJpaSandboxApplication.class);

    @Autowired
    private BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaSandboxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
/*
        log.info("StartApplication...");

        bookRepository.save(new Book("Java"));
        bookRepository.save(new Book("Node"));
        bookRepository.save(new Book("Python"));

        System.out.println("\nfindAll()");
        bookRepository.findAll().forEach(x -> System.out.println(x));

        System.out.println("\nfindById(1L)");
        bookRepository.findById(1l).ifPresent(x -> System.out.println(x));

        System.out.println("\nfindByName('Node')");
        bookRepository.findByName("Node").forEach(x -> System.out.println(x));
 */
    }


}
