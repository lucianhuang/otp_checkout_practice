package com.example.openticket.repository;
import com.example.openticket.entity.EventTicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface EventTicketTypeRepository extends JpaRepository<EventTicketType, Long> {
    // 悲觀鎖查詢
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EventTicketType> findWithLockById(Long id);
}