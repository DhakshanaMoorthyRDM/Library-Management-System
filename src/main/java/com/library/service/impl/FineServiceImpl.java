package com.library.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.entity.BorrowRecord;
import com.library.entity.Fine;
import com.library.exception.BorrowException;
import com.library.repository.BorrowRepository;
import com.library.repository.FineRepository;
import com.library.service.FineService;

@Service
public class FineServiceImpl implements FineService {

    private static final Logger logger =
            LoggerFactory.getLogger(FineServiceImpl.class);

    private static final BigDecimal FINE_PER_DAY =
            BigDecimal.TEN;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private BorrowRepository borrowRepository;


    // =========================
    // CALCULATE FINE
    // =========================

    @Override
    public Fine calculateFine(Long borrowId) {

        logger.info(
                "Calculating fine for Borrow ID: {}",
                borrowId);

        BorrowRecord borrow =
                borrowRepository.findById(borrowId)
                        .orElse(null);

        if (borrow == null) {

            logger.warn(
                    "Borrow record not found: {}",
                    borrowId);

            throw new BorrowException(
                    "Borrow record not found");
        }


        // =========================
        // DETERMINE END DATE
        // =========================

        LocalDate endDate;

        if (Boolean.TRUE.equals(
                borrow.getReturned())) {

            endDate =
                    borrow.getActualReturnDate();

        } else {

            endDate =
                    LocalDate.now();
        }


        // =========================
        // CHECK OVERDUE
        // =========================

        if (!endDate.isAfter(
                borrow.getExpectedReturnDate())) {

            logger.info(
                    "Borrow ID {} is not overdue. No fine created.",
                    borrowId);

            return null;
        }


        // =========================
        // CALCULATE OVERDUE DAYS
        // =========================

        long overdueDays =
                ChronoUnit.DAYS.between(
                        borrow.getExpectedReturnDate(),
                        endDate);


        // =========================
        // CALCULATE AMOUNT
        // =========================

        BigDecimal amount =
                FINE_PER_DAY.multiply(
                        BigDecimal.valueOf(overdueDays));


        // =========================
        // FIND EXISTING FINE
        // OR CREATE NEW FINE
        // =========================

        Fine fine =
                fineRepository
                        .findByBorrowId(borrowId)
                        .orElse(new Fine());

        fine.setBorrowId(borrowId);
        fine.setAmount(amount);

        if (fine.getPaid() == null) {

            fine.setPaid(false);
        }


        // =========================
        // SAVE FINE
        // =========================

        Fine savedFine =
                fineRepository.save(fine);

        logger.info(
                "Fine updated successfully. Borrow ID: {}, Overdue Days: {}, Amount: {}",
                borrowId,
                overdueDays,
                amount);

        return savedFine;
    }


    // =========================
    // GET FINE BY BORROW ID
    // =========================

    @Override
    public Fine getFineByBorrowId(
            Long borrowId) {

        logger.info(
                "Fetching fine for Borrow ID: {}",
                borrowId);

        Fine fine =
                fineRepository
                        .findByBorrowId(borrowId)
                        .orElse(null);

        if (fine == null) {

            logger.warn(
                    "Fine not found for Borrow ID: {}",
                    borrowId);

            throw new BorrowException(
                    "Fine not found");
        }

        logger.info(
                "Fine found for Borrow ID: {}",
                borrowId);

        return fine;
    }


    // =========================
    // GET ALL FINES
    // =========================

    @Override
    public List<Fine> getAllFines() {

        logger.info(
                "Fetching all fines");

        List<Fine> fines =
                fineRepository.findAll();

        logger.info(
                "Total fines found: {}",
                fines.size());

        return fines;
    }

    @Override
    public Fine payFine(Long id) {

        Fine fine = fineRepository.findById(id)
                .orElse(null);

        if (fine == null) {
            throw new BorrowException("Fine not found");
        }

        if (Boolean.TRUE.equals(fine.getPaid())) {
            throw new BorrowException("Fine is already paid");
        }

        fine.setPaid(true);

        return fineRepository.save(fine);
    }
}
