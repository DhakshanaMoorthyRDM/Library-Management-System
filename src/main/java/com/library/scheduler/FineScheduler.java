package com.library.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.library.entity.BorrowRecord;
import com.library.service.FineService;

@Component
public class FineScheduler {

    private static final Logger logger =
            LoggerFactory.getLogger(FineScheduler.class);

    @Autowired
    private FineService fineService;

    @Autowired
    private com.library.repository.BorrowRepository borrowRepository;


    // Runs every day at midnight
    @Scheduled(cron = "0 24 20 * * *")
    public void updateFines() {

        logger.info(
                "Fine scheduler started");

        List<BorrowRecord> borrows =
                borrowRepository.findAll();

        for (BorrowRecord borrow : borrows) {

            if (!Boolean.TRUE.equals(
                    borrow.getReturned())) {

                fineService.calculateFine(
                        borrow.getId());
            }
        }

        logger.info(
                "Fine scheduler completed");
    }
}