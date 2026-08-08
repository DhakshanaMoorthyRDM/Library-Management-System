package com.library.service;

import java.util.List;

import com.library.entity.BorrowRecord;

public interface BorrowService {

    BorrowRecord issueBook(BorrowRecord borrowRecord);

    BorrowRecord getBorrowById(Long id);

    List<BorrowRecord> getAllBorrows();

    BorrowRecord returnBook(Long id);

    BorrowRecord renewBook(Long id);

    List<BorrowRecord> getUserBorrowHistory(Long userId);
}