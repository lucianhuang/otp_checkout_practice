package com.example.openticket.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders") 
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_type")
    private String invoiceType;

    @Column(name = "invoice_tax_id")
    private String invoiceTaxId;

    @Column(name = "status")
    private String status; // PENDING，監測金流狀態

    @Column(name = "reservations_id")
    private Long reservationsId;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}

