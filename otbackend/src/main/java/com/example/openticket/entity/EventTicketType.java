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

    // 這張表屬於哪個活動
    @Column(name = "event_id")
    private Long eventId;

    // 用這個 ID 才能去 TicketType 查到 "一般票" 這個名字
    @Column(name = "ticket_template_id")
    private Long ticketTemplateId;

    @Column(name = "custom_limit")
    private Integer customLimit; // 這就是庫存

    // 因為 Service 要用 .getCustomPrice() 拿到資料庫裡的價格
    @Column(name = "custom_price")
    private BigDecimal customPrice;
}