package com.library.service.impl;

import java.time.LocalDate;
import java.util.List;

import com.library.service.FineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.User;
import com.library.exception.BorrowException;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRepository;
import com.library.repository.UserRepository;
import com.library.service.BorrowService;

@Service
public class BorrowServiceImpl implements BorrowService {

    private static final Logger logger =
            LoggerFactory.getLogger(BorrowServiceImpl.class);

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FineService fineService;


    // =========================
    // ISSUE BOOK
    // =========================

    @Override
    @Transactional
    public BorrowRecord issueBook(BorrowRecord borrowRecord) {

        logger.info(
                "Issue request received. User ID: {}, Book ID: {}",
                borrowRecord.getUserId(),
                borrowRecord.getBookId());

        // Check user
        User user = userRepository
                .findById(borrowRecord.getUserId())
                .orElse(null);

        if (user == null) {

            logger.warn(
                    "Issue failed. User not found: {}",
                    borrowRecord.getUserId());

            throw new BorrowException("User not found");
        }

        // Check user status
        if (!"ACTIVE".equals(user.getStatus())) {

            logger.warn(
                    "Issue failed. User {} is INACTIVE",
                    user.getId());

            throw new BorrowException("User is inactive");
        }

        // Check book
        Book book = bookRepository
                .findById(borrowRecord.getBookId())
                .orElse(null);

        if (book == null) {

            logger.warn(
                    "Issue failed. Book not found: {}",
                    borrowRecord.getBookId());

            throw new BorrowException("Book not found");
        }

        // Check book status
        if (!"ACTIVE".equals(book.getStatus())) {

            logger.warn(
                    "Issue failed. Book {} is INACTIVE",
                    book.getId());

            throw new BorrowException("Book is inactive");
        }

        // Check available copies
        if (book.getAvailableCopies() <= 0) {

            logger.warn(
                    "Issue failed. Book {} has no available copies",
                    book.getId());

            throw new BorrowException("No copies available");
        }

        // Check if user already has this book
        List<BorrowRecord> existingBorrows =
                borrowRepository.findByUserIdAndBookIdAndReturnedFalse(
                        user.getId(),
                        book.getId());

        if (!existingBorrows.isEmpty()) {

            logger.warn(
                    "Issue failed. User {} already has Book {}",
                    user.getId(),
                    book.getId());

            throw new BorrowException(
                    "User already has this book");
        }

        // Set issue date
        LocalDate issueDate = LocalDate.now();

        borrowRecord.setIssueDate(issueDate);

        // 14 days borrowing period
        borrowRecord.setExpectedReturnDate(
                issueDate.plusDays(14));

        borrowRecord.setActualReturnDate(null);
        borrowRecord.setRenewCount(0);
        borrowRecord.setReturned(false);

        // Save borrow record
        BorrowRecord savedBorrow =
                borrowRepository.save(borrowRecord);

        // Reduce available copies
        book.setAvailableCopies(
                book.getAvailableCopies() - 1);

        bookRepository.save(book);

        logger.info(
                "Book issued successfully. Borrow ID: {}, User ID: {}, Book ID: {}",
                savedBorrow.getId(),
                user.getId(),
                book.getId());

        return savedBorrow;
    }


    // =========================
    // GET BORROW BY ID
    // =========================

    @Override
    public BorrowRecord getBorrowById(Long id) {

        logger.info(
                "Fetching borrow record with ID: {}",
                id);

        BorrowRecord borrowRecord =
                borrowRepository.findById(id)
                        .orElse(null);

        if (borrowRecord == null) {

            logger.warn(
                    "Borrow record not found: {}",
                    id);

            throw new BorrowException(
                    "Borrow record not found");
        }

        logger.info(
                "Borrow record found: {}",
                id);

        return borrowRecord;
    }


    // =========================
    // GET ALL BORROWS
    // =========================

    @Override
    public List<BorrowRecord> getAllBorrows() {

        logger.info(
                "Fetching all borrow records");

        List<BorrowRecord> borrows =
                borrowRepository.findAll();

        logger.info(
                "Total borrow records found: {}",
                borrows.size());

        return borrows;
    }


    // =========================
    // RETURN BOOK
    // =========================

    @Override
    @Transactional
    public BorrowRecord returnBook(Long id) {

        logger.info(
                "Return request received for Borrow ID: {}",
                id);

        BorrowRecord borrowRecord =
                borrowRepository.findById(id)
                        .orElse(null);

        if (borrowRecord == null) {

            logger.warn(
                    "Return failed. Borrow record not found: {}",
                    id);

            throw new BorrowException(
                    "Borrow record not found");
        }

        // Already returned
        if (Boolean.TRUE.equals(
                borrowRecord.getReturned())) {

            logger.warn(
                    "Return failed. Borrow ID {} is already returned",
                    id);

            throw new BorrowException(
                    "Book has already been returned");
        }

        // Find book
        Book book = bookRepository
                .findById(borrowRecord.getBookId())
                .orElse(null);

        if (book == null) {

            logger.warn(
                    "Return failed. Book not found: {}",
                    borrowRecord.getBookId());

            throw new BorrowException(
                    "Book not found");
        }

        // Update borrow record
        borrowRecord.setActualReturnDate(
                LocalDate.now());

        borrowRecord.setReturned(true);

        BorrowRecord updatedBorrow =
                borrowRepository.save(borrowRecord);

        // Increase available copies
        book.setAvailableCopies(
                book.getAvailableCopies() + 1);

        bookRepository.save(book);
        fineService.calculateFine(id);

        logger.info(
                "Book returned successfully. Borrow ID: {}, Book ID: {}",
                id,
                book.getId());

        return updatedBorrow;
    }


    // =========================
    // RENEW BOOK
    // =========================

    @Override
    @Transactional
    public BorrowRecord renewBook(Long id) {

        logger.info(
                "Renew request received for Borrow ID: {}",
                id);

        BorrowRecord borrowRecord =
                borrowRepository.findById(id)
                        .orElse(null);

        if (borrowRecord == null) {

            logger.warn(
                    "Renew failed. Borrow record not found: {}",
                    id);

            throw new BorrowException(
                    "Borrow record not found");
        }

        // Already returned
        if (Boolean.TRUE.equals(
                borrowRecord.getReturned())) {

            logger.warn(
                    "Renew failed. Borrow ID {} is already returned",
                    id);

            throw new BorrowException(
                    "Book has already been returned");
        }

        // Only one renewal allowed
        if (borrowRecord.getRenewCount() >= 1) {

            logger.warn(
                    "Renew failed. Borrow ID {} has already been renewed",
                    id);

            throw new BorrowException(
                    "Book can only be renewed once");
        }

        // Cannot renew after due date
        LocalDate today = LocalDate.now();

        if (today.isAfter(
                borrowRecord.getExpectedReturnDate())) {

            logger.warn(
                    "Renew failed. Borrow ID {} is overdue",
                    id);

            throw new BorrowException(
                    "Book cannot be renewed after the due date");
        }

        // Extend by 14 days
        borrowRecord.setExpectedReturnDate(
                borrowRecord.getExpectedReturnDate()
                        .plusDays(14));

        borrowRecord.setRenewCount(1);

        BorrowRecord updatedBorrow =
                borrowRepository.save(borrowRecord);

        logger.info(
                "Book renewed successfully. Borrow ID: {}, New return date: {}",
                id,
                updatedBorrow.getExpectedReturnDate());

        return updatedBorrow;
    }


    // =========================
    // USER BORROW HISTORY
    // =========================

    @Override
    public List<BorrowRecord> getUserBorrowHistory(
            Long userId) {

        logger.info(
                "Fetching borrow history for User ID: {}",
                userId);

        List<BorrowRecord> userBorrows =
                borrowRepository.findByUserId(userId);

        logger.info(
                "Found {} borrow records for User ID: {}",
                userBorrows.size(),
                userId);

        return userBorrows;
    }
}

