package com.example.openticket.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // 之後檢查 5 分鐘要用，但測試的時候乾脆用 10 秒就好

    @Column(name = "status")
    private String status;
    
    @Column(name = "totalAmount") 
    private BigDecimal totalAmount; 
}
