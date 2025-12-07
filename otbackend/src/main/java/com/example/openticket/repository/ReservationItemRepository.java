package com.example.openticket.repository;
import com.example.openticket.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {
    // 找出這張預約單買了哪些東西
    List<ReservationItem> findByReservationsId(Long reservationsId);
    
}