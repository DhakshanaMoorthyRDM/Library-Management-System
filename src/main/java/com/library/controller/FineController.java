package com.library.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.entity.Fine;
import com.library.exception.ErrorResponse;
import com.library.service.FineService;

@RestController
@RequestMapping("/fines")
public class FineController {

    private static final Logger logger =
            LoggerFactory.getLogger(FineController.class);

    @Autowired
    private FineService fineService;


    // =========================
    // CALCULATE FINE
    // =========================

    @PostMapping("/calculate/{borrowId}")
    public ResponseEntity<?> calculateFine(
            @PathVariable Long borrowId) {

        logger.info(
                "Fine calculation request for Borrow ID: {}",
                borrowId);

        Fine fine =
                fineService.calculateFine(borrowId);

        if (fine == null) {

            logger.info(
                    "No fine for Borrow ID: {}",
                    borrowId);

            return ResponseEntity.ok(
                    new ErrorResponse(
                            "No fine for this borrow",
                            "/fines/calculate/" + borrowId));
        }

        logger.info(
                "Fine calculated successfully for Borrow ID: {}",
                borrowId);

        return ResponseEntity.ok(fine);
    }


    // =========================
    // GET FINE BY BORROW ID
    // =========================

    @GetMapping("/borrow/{borrowId}")
    public ResponseEntity<?> getFine(
            @PathVariable Long borrowId) {

        logger.info(
                "Fetching fine for Borrow ID: {}",
                borrowId);

        Fine fine =
                fineService.getFineByBorrowId(borrowId);

        if (fine == null) {

            logger.warn(
                    "Fine not found for Borrow ID: {}",
                    borrowId);

            return ResponseEntity
                    .status(404)
                    .body(new ErrorResponse(
                            "Fine not found",
                            "/fines/borrow/" + borrowId));
        }

        logger.info(
                "Fine found for Borrow ID: {}",
                borrowId);

        return ResponseEntity.ok(fine);
    }


    // =========================
    // GET ALL FINES
    // =========================

    @GetMapping
    public ResponseEntity<List<Fine>> getAllFines() {

        logger.info(
                "Fetching all fines");

        List<Fine> fines =
                fineService.getAllFines();

        logger.info(
                "Total fines found: {}",
                fines.size());

        return ResponseEntity.ok(fines);
    }
}
