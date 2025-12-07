package com.example.openticket.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "checkout_orders")
public class CheckoutOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "event_ticket_type_id")
    private Long eventTicketTypeId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unit_price")
    
    // 這裡對應好資料庫的 unit_price 了
    private BigDecimal unitPrice; 
}