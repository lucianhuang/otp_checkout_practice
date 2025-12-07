package com.example.openticket.controller;


import com.example.openticket.entity.EventTicketType; 
import com.example.openticket.entity.TicketType;     

import com.example.openticket.dto.ReservationCheckoutDto;
import com.example.openticket.entity.Reservation;
import com.example.openticket.entity.ReservationItem; 
import com.example.openticket.repository.EventTicketTypeRepository;
import com.example.openticket.repository.ReservationItemRepository; 

import com.example.openticket.repository.ReservationRepository;
import com.example.openticket.repository.TicketTypeRepository;
import com.example.openticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private EventTicketTypeRepository eventTicketTypeRepository;
    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationItemRepository reservationItemRepository; // 注入明細的倉管

@GetMapping("/{id}/checkout")
    public ResponseEntity<?> getCheckoutInfo(@PathVariable Long id) {
        
        // 找預約單
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到預約單 ID: " + id));

        ReservationCheckoutDto dto = new ReservationCheckoutDto();
        dto.setReservationId(reservation.getId());

        // 補上使用者資訊
        if (reservation.getUserId() != null) {
            userRepository.findById(reservation.getUserId()).ifPresent(user -> {
                dto.setUserName(user.getUsername());
                dto.setUserEmail(user.getAccount());
            });
        } else {
            dto.setUserName("訪客");
            dto.setUserEmail("無");
        }

        // 撈明細並準備計算
        List<ReservationItem> items = reservationItemRepository.findByReservationsId(id);
        
        // 準備一個變數來累加，初始為 0
        java.math.BigDecimal calculatedTotal = java.math.BigDecimal.ZERO;

        if (items != null) {
            for (ReservationItem item : items) {
                // 查票名邏輯 
                String ticketName = "未知票種";
                EventTicketType ett = eventTicketTypeRepository.findById(item.getEventTicketTypeId()).orElse(null);
                if (ett != null) {
                    TicketType tt = ticketTypeRepository.findById(ett.getTicketTemplateId()).orElse(null);
                    if (tt != null) {
                        ticketName = tt.getName();
                    }
                }
                // .....................................

                // 拿到單價跟數量，並累加總金額
                java.math.BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : java.math.BigDecimal.ZERO;
                java.math.BigDecimal quantity = new java.math.BigDecimal(item.getQuantity());
                
                // 累加：總數 = 總數 + (單價 * 數量)
                calculatedTotal = calculatedTotal.add(unitPrice.multiply(quantity));

                dto.getItems().add(new ReservationCheckoutDto.ItemDto(
                    ticketName,
                    unitPrice, 
                    item.getQuantity()
                ));
            }
        }
        
        // 雙保險邏輯：優先用資料庫的，沒值才用算的
        if (reservation.getTotalAmount() != null) {
            // 如果資料庫有值，尊重前一手的資料
            dto.setTotalAmount(reservation.getTotalAmount());
        } else {
            // 如果資料庫是 null，我自己算
            dto.setTotalAmount(calculatedTotal);
        }

        return ResponseEntity.ok(dto);
    }
}