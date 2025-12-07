package com.example.openticket.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user") // 對應資料庫的 user 表
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String account; // 這也是 email

    @Column(nullable = false)
    private String password;

    private String username;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 其他欄位如 address, tel 若暫時用不到可先不寫
    // private String address;
    // private String tel;
}