package com.example.openticket.entity;

import jakarta.persistence.*;
import lombok.Data;

// 預約明細 - 扣庫存要用

@Entity
@Data
@Table(name = "reservation_items")
public class ReservationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservations_id")
    private Long reservationsId;

    @Column(name = "event_ticket_type_id")
    private Long eventTicketTypeId;

    @Column(name = "quantity")
    private Integer quantity;
}