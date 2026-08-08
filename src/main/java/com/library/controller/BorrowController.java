package com.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import com.library.entity.User;
import com.library.service.UserService;

import com.library.entity.BorrowRecord;
import com.library.service.BorrowService;

@RestController
@RequestMapping("/borrows")
public class BorrowController {

    private static final Logger logger =
            LoggerFactory.getLogger(BorrowController.class);

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;


    // =========================
    // ISSUE BOOK
    // =========================

    @PostMapping
    public ResponseEntity<BorrowRecord> issueBook(
            @RequestBody BorrowRecord borrowRecord) {

        logger.info(
                "Issue request received. User ID: {}, Book ID: {}",
                borrowRecord.getUserId(),
                borrowRecord.getBookId());

        BorrowRecord savedBorrow =
                borrowService.issueBook(borrowRecord);

        if (savedBorrow == null) {

            logger.warn("Book issue failed");

            return ResponseEntity.badRequest().build();
        }

        logger.info(
                "Book issued successfully. Borrow ID: {}",
                savedBorrow.getId());

        return ResponseEntity.status(201).body(savedBorrow);
    }


    // =========================
    // GET ALL BORROWS
    // =========================

    @GetMapping
    public ResponseEntity<List<BorrowRecord>> getAllBorrows() {

        logger.info("Fetching all borrow records");

        List<BorrowRecord> borrows =
                borrowService.getAllBorrows();

        return ResponseEntity.ok(borrows);
    }


    // =========================
    // GET BORROW BY ID
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<BorrowRecord> getBorrowById(
            @PathVariable Long id) {

        logger.info("Fetching borrow record: {}", id);

        BorrowRecord borrowRecord =
                borrowService.getBorrowById(id);

        if (borrowRecord == null) {

            logger.warn(
                    "Borrow record not found: {}",
                    id);

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(borrowRecord);
    }


    // =========================
    // RETURN BOOK
    // =========================

    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowRecord> returnBook(
            @PathVariable Long id) {

        logger.info(
                "Return request received for Borrow ID: {}",
                id);

        BorrowRecord returnedBorrow =
                borrowService.returnBook(id);

        if (returnedBorrow == null) {

            logger.warn(
                    "Book return failed for Borrow ID: {}",
                    id);

            return ResponseEntity.badRequest().build();
        }

        logger.info(
                "Book returned successfully. Borrow ID: {}",
                id);

        return ResponseEntity.ok(returnedBorrow);
    }


    // =========================
    // RENEW BOOK
    // =========================

    @PutMapping("/{id}/renew")
    public ResponseEntity<BorrowRecord> renewBook(
            @PathVariable Long id) {

        logger.info(
                "Renew request received for Borrow ID: {}",
                id);

        BorrowRecord renewedBorrow =
                borrowService.renewBook(id);

        if (renewedBorrow == null) {

            logger.warn(
                    "Book renewal failed for Borrow ID: {}",
                    id);

            return ResponseEntity.badRequest().build();
        }

        logger.info(
                "Book renewed successfully. Borrow ID: {}",
                id);

        return ResponseEntity.ok(renewedBorrow);
    }


    // =========================
    // USER BORROW HISTORY
    // =========================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BorrowRecord>> getUserBorrowHistory(
            @PathVariable Long userId,
            Authentication authentication) {

        logger.info("Fetching borrow history for User ID: {}", userId);

        String loggedInEmail = authentication.getName();

        User loggedInUser = userService.getUserByEmail(loggedInEmail);

        if (loggedInUser == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isAdmin = "ADMIN".equals(loggedInUser.getRole());

        if (!isAdmin && !loggedInUser.getId().equals(userId)) {
            logger.warn(
                    "User {} attempted to access borrow history of User {}",
                    loggedInUser.getId(),
                    userId);

            return ResponseEntity.status(403).build();
        }

        List<BorrowRecord> borrows =
                borrowService.getUserBorrowHistory(userId);

        return ResponseEntity.ok(borrows);
    }
}