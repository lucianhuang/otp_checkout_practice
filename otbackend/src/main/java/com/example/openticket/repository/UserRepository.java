package com.example.openticket.repository;

import com.example.openticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 透過 ID 找用戶，but，其實 JpaRepository 內建就有 findById
    
    // 如果之後要用 email 登入，可以加這行：
    Optional<User> findByAccount(String account);
}