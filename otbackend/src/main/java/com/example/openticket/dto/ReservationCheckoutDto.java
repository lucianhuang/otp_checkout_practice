package com.example.openticket.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReservationCheckoutDto {
    private Long reservationId;
    private BigDecimal totalAmount;
    private String userName;     // 買票的人
    private String userEmail;    // 聯絡信箱，名稱可以跟資料表不同
    private List<ItemDto> items = new ArrayList<>(); // 買了什麼票

    // --- Getters & Setters ---
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<ItemDto> getItems() { return items; }
    public void setItems(List<ItemDto> items) { this.items = items; }

    // 內部的類別，用來裝每一行明細 (例如：一般票 2張)
    public static class ItemDto {
        private String ticketName;
        private BigDecimal price;
        private Integer quantity;

        // 建構子方便賦值
        public ItemDto(String ticketName, BigDecimal price, Integer quantity) {
            this.ticketName = ticketName;
            this.price = price;
            this.quantity = quantity;
        }

        public String getTicketName() { return ticketName; }
        public BigDecimal getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
    }
}