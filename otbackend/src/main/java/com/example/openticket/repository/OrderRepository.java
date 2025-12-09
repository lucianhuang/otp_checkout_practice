package com.example.openticket.repository;

import com.example.openticket.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    boolean existsByReservationsId(Long reservationsId);

    // 讓 Service 可以把舊訂單抓出來檢查狀態
    Optional<Order> findByReservationsId(Long reservationsId);
}