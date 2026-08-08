package com.library.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.entity.Book;
import com.library.repository.BookRepository;
import com.library.service.BookService;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id).orElse(null);

        if (existingBook == null) {
            return null;
        }

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setEdition(book.getEdition());
        existingBook.setLanguage(book.getLanguage());
        existingBook.setPublicationYear(book.getPublicationYear());
        existingBook.setDescription(book.getDescription());
        existingBook.setCategory(book.getCategory());
        existingBook.setTotalCopies(book.getTotalCopies());
        existingBook.setAvailableCopies(book.getAvailableCopies());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);

        if (book != null) {
            book.setStatus("INACTIVE");
            bookRepository.save(book);
        }
    }
}