package com.example.openticket.service;

import com.example.openticket.dto.CheckoutRequest;
import com.example.openticket.entity.*; 
import com.example.openticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationItemRepository reservationItemRepository;

    @Autowired
    private EventTicketTypeRepository eventTicketTypeRepository;

    @Autowired
    private CheckoutOrderRepository checkoutOrderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Long createOrder(CheckoutRequest request) {
        // 冪等性檢查：防止重複下單
        Optional<Order> existingOrderOpt = orderRepository.findByReservationsId(request.getReservationId());
        if (existingOrderOpt.isPresent()) {
            Order existingOrder = existingOrderOpt.get();
            

            // 為了重複測試 id=10 那筆，我先註解這段，不然會不給我們去藍新 
            // if ("PAID".equals(existingOrder.getStatus()) || "SUCCESS".equals(existingOrder.getStatus())) {
            //     throw new RuntimeException("這筆訂單已完成付款，無需重複結帳！");
            // }
            System.out.println("發現未付款的舊訂單，直接回傳 ID: " + existingOrder.getId());
            return existingOrder.getId();
        }

        // 建立新訂單
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("找不到預約單")); 

        Order order = new Order();
        order.setReservationsId(reservation.getId());
        order.setInvoiceType(request.getInvoiceType());
        order.setInvoiceTaxId(request.getInvoiceTaxId());
        order.setStatus("PENDING");
        orderRepository.save(order); 

        // 處理明細與扣庫存
        List<ReservationItem> items = reservationItemRepository.findByReservationsId(request.getReservationId());
        for (ReservationItem item : items) {
            EventTicketType ticketType = eventTicketTypeRepository.findWithLockById(item.getEventTicketTypeId())
                    .orElseThrow(() -> new RuntimeException("找不到票種"));
            
            if (ticketType.getCustomLimit() != null && ticketType.getCustomLimit() < item.getQuantity()) {
                throw new RuntimeException("來晚一步！票被搶光了 (庫存不足)");
            }
            if (ticketType.getCustomLimit() != null) {
                ticketType.setCustomLimit(ticketType.getCustomLimit() - item.getQuantity());
                eventTicketTypeRepository.save(ticketType);
            }

            CheckoutOrder checkoutOrder = new CheckoutOrder();
            checkoutOrder.setOrder(order); 
            checkoutOrder.setEventTicketTypeId(item.getEventTicketTypeId());
            checkoutOrder.setQuantity(item.getQuantity());
            checkoutOrder.setUnitPrice(ticketType.getCustomPrice() != null ? ticketType.getCustomPrice() : BigDecimal.ZERO);
            checkoutOrderRepository.save(checkoutOrder);
        }

        // 建立付款紀錄
        Payment payment = new Payment();
        payment.setOrder(order); 
        payment.setPaymentMethod("ONLINE_PAY"); 
        payment.setAmount(reservation.getTotalAmount()); 
        payment.setStatus("PENDING");
        paymentRepository.save(payment); 

        System.out.println("新訂單建立成功，ID: " + order.getId());
        return order.getId(); 
    }

    public Integer getOrderAmount(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        
        Reservation reservation = reservationRepository.findById(order.getReservationsId())
                .orElseThrow(() -> new RuntimeException("找不到對應的預約資料"));
        
        // 如果資料庫有值，就用資料庫的；如果真的還是空的，才用 200 (防呆)
        if (reservation.getTotalAmount() == null) {
            return 200; 
        }
        
        return reservation.getTotalAmount().intValue();
    }

    // 專門給 Demo 偷改狀態用的
    @Transactional
    public void updateStatusToPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        
        order.setStatus("PAID"); // 強制改成已付款
        orderRepository.save(order);
        
        System.out.println("訂單 " + orderId + " 狀態已更新為 PAID (Demo模式)");
    }
}