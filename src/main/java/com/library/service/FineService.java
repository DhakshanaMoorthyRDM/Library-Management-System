package com.library.service;

import java.util.List;

import com.library.entity.Fine;

public interface FineService {

    Fine calculateFine(Long borrowId);

    Fine getFineByBorrowId(Long borrowId);

    List<Fine> getAllFines();
}
