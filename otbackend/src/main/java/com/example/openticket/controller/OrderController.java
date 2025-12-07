package com.example.openticket.controller;

import com.example.openticket.dto.CheckoutRequest;
import com.example.openticket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController // 告訴 Spring：這是接受 HTTP 請求的路徑
@RequestMapping("/api/orders") // 路徑
public class OrderController {

    @Autowired
    // 注入 注入了 OrderService 這個類別的物件 (Object)，
    // 並把它取名為 orderService。
    private OrderService orderService; 
    
    // 前端按下「結帳」按鈕時，會打這個 API
    @PostMapping("/checkout")
    public String checkout(@RequestBody CheckoutRequest request) {
        // 把單子交給主廚，並拿到訂單編號

        // 呼叫 Service 層的方法，
        // 開始執行那些複雜的邏輯（扣庫存、算錢、存資料庫）。

        // Gemini 的比喻：
        // 服務生 (controller) 拿著客人寫好的菜單 (request)，
        // 轉頭大喊：「主廚 (orderService)！請按照這張單子做菜 (createOrder)！」
        
        Long orderId = orderService.createOrder(request);

        // 主廚把這個號碼回傳 (Return) 給服務生。
        return "結帳成功！訂單 ID: " + orderId;
    }
}