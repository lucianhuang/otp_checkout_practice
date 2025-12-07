package com.example.openticket.repository;

import com.example.openticket.entity.EventTicketType;
import com.example.openticket.entity.TicketType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    
    // 悲觀鎖查詢 (給結帳扣庫存用)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EventTicketType> findWithLockById(Long id);

    // 繼承 JpaRepository 後，其實內建就有 findById(Long id) 了

}