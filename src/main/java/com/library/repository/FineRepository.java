
package com.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entity.Fine;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByBorrowId(Long borrowId);
}

