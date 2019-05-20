package com.marco.springdatajpasandbox.persistence;

import org.springframework.beans.factory.annotation.Value;

public interface NamesOnly {

    String getName();

    @Value("#{target.name + ' (' + target.author.name + ')'}")
    String getNameAndAuthor();
}
