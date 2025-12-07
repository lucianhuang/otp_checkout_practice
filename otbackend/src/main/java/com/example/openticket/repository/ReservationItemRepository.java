package com.example.openticket.repository;

import com.example.openticket.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; 

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    // 定義這個方法，讓 Spring Data JPA 自動寫 SQL
    // 去資料庫找 reservationsId 等於傳入值的那些資料
    List<ReservationItem> findByReservationsId(Long reservationsId);
    
}