package com.example.openticket.repository;
import com.example.openticket.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByReservationsId(Long reservationsId);
}