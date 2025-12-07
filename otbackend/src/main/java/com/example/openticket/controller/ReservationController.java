package com.example.openticket.controller;


import com.example.openticket.entity.EventTicketType; 
import com.example.openticket.entity.TicketType;     

import com.example.openticket.dto.ReservationCheckoutDto;
import com.example.openticket.entity.Reservation;
import com.example.openticket.entity.ReservationItem; // 記得引入這個
import com.example.openticket.repository.EventTicketTypeRepository;
import com.example.openticket.repository.ReservationItemRepository; // 記得引入 Repository
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

    @GetMapping("/{id}/checkout-info")
    public ResponseEntity<?> getCheckoutInfo(@PathVariable Long id) {
        
        // 找預約單
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到預約單 ID: " + id));

        // DTO
        ReservationCheckoutDto dto = new ReservationCheckoutDto();
        dto.setReservationId(reservation.getId());
        dto.setTotalAmount(reservation.getTotalAmount());

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

        // 補上明細 
        // 改用 Repository 去撈，而不是從 reservation 拿
        List<ReservationItem> items = reservationItemRepository.findByReservationsId(id);
        
        if (items != null) {
            items.forEach(item -> {
                // 動態查詢
                String ticketName = "未知票種";
                
                // 先用 ID 查出 EventTicketType (活動票種設定)
                EventTicketType ett = eventTicketTypeRepository.findById(item.getEventTicketTypeId()).orElse(null);
                
                if (ett != null) {
                    // 再拿裡面的 ticketTemplateId 去查 TicketType (票種名稱)
                    TicketType tt = ticketTypeRepository.findById(ett.getTicketTemplateId()).orElse(null);
                    if (tt != null) {
                        ticketName = tt.getName(); // 拿到 "一般票" 或 "兒童票"
                    }
                }
                // ----------------------------------------

                dto.getItems().add(new ReservationCheckoutDto.ItemDto(
                    ticketName,
                    item.getUnitPrice() != null ? item.getUnitPrice() : java.math.BigDecimal.ZERO, 
                    item.getQuantity()
                ));
            });
        }

        return ResponseEntity.ok(dto);
    }
}