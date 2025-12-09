package com.example.openticket.controller;

import com.example.openticket.dto.CheckoutRequest;
import com.example.openticket.service.NewebPayService;
import com.example.openticket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private NewebPayService newebPayService;

    @PostMapping("/checkout")
    public String checkout(@RequestBody CheckoutRequest request) {
        // 建立訂單 (此時資料庫狀態應該是 PENDING)
        Long orderId = orderService.createOrder(request);

        // 取得金額
        Integer amount = orderService.getOrderAmount(orderId);
        
        // 可以在這裡呼叫 service 直接把 orderId 的狀態改成 PAID
        // 藍新的幕後處理，不能用8080的API呼叫，但我們先完成開發

        orderService.updateStatusToPaid(orderId); 

        System.out.println("準備前往藍新金流，訂單ID: " + orderId + ", 金額: " + amount);

        // 產生藍新表單 HTML
        // 這會回傳一個自動 submit 的 HTML，前端 React 接收到後要把它顯示出來
        return newebPayService.genCheckOutForm(request, amount, orderId);
    }
}