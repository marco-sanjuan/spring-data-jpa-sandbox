package com.marco.springdatajpasandbox.persistence;


import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = Book.class, idClass = Long.class)
public interface BookCustomRepository {

    List<Book> findByName(String name);

}
