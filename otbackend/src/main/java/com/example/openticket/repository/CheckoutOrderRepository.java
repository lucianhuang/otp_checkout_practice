package com.example.openticket.repository;
import com.example.openticket.entity.CheckoutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CheckoutOrderRepository extends JpaRepository<CheckoutOrder, Long> {}