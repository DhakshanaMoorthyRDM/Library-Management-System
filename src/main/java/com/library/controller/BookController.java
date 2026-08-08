package com.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.entity.Book;
import com.library.exception.ErrorResponse;
import com.library.service.BookService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<?> saveBook(@RequestBody Book book, HttpServletRequest request) {
        logger.info("Creating book: {}", book.getTitle());

        Book savedBook = bookService.saveBook(book);

        if (savedBook == null) {
            logger.warn("Book creation failed");
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Book could not be created", request.getRequestURI()));
        }

        logger.info("Book created successfully with id: {}", savedBook.getId());
        return ResponseEntity.status(201).body(savedBook);
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("Fetching all books");

        List<Book> books = bookService.getAllBooks();

        logger.info("Total books found: {}", books.size());
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id, HttpServletRequest request) {
        logger.info("Fetching book with id: {}", id);

        Book book = bookService.getBookById(id);

        if (book == null) {
            logger.warn("Book not found with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Book not found", request.getRequestURI()));
        }

        logger.info("Book found with id: {}", id);
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book book,
                                        HttpServletRequest request) {
        logger.info("Updating book with id: {}", id);

        Book updatedBook = bookService.updateBook(id, book);

        if (updatedBook == null) {
            logger.warn("Book not found with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Book not found", request.getRequestURI()));
        }

        logger.info("Book updated successfully with id: {}", id);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id, HttpServletRequest request) {
        logger.info("Deactivating book with id: {}", id);

        Book book = bookService.getBookById(id);

        if (book == null) {
            logger.warn("Book not found with id: {}", id);
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Book not found", request.getRequestURI()));
        }

        bookService.deleteBook(id);

        logger.info("Book deactivated successfully with id: {}", id);
        return ResponseEntity.ok(
                new ErrorResponse("Book deactivated successfully", request.getRequestURI()));
    }
}