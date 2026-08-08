package com.library.service;

import java.util.List;

import com.library.entity.Book;

public interface BookService {

    Book saveBook(Book book);

    List<Book> getAllBooks();

    Book getBookById(Long id);

    Book updateBook(Long id, Book book);

    void deleteBook(Long id);
}