package com.example.openticket.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

// 票種庫存，就是被扣的對象

@Entity
@Data
@Table(name = "event_ticket_type")
public class EventTicketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "custom_limit")
    private Integer customLimit; // 這就是庫存

    // 因為 Service 要用 .getCustomPrice() 拿到資料庫裡的價格
    @Column(name = "custom_price")
    private BigDecimal customPrice;
}