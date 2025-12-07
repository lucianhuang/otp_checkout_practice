package com.example.openticket.dto;

import lombok.Data;

// 這邊功能是：Frontend/Controller，反正就是等前端傳資料來

@Data

// 這是前端結帳時會傳給來的三個參數
public class CheckoutRequest {
    
    // 預約單號 (最重要，用來查庫存)
    private Long reservationId;  
    
    // 發票種類 (PERSONAL / COMPANY)
    private String invoiceType; 
    
    // 統一編號 (如果是公司發票才有值)
    private String invoiceTaxId; 
    
}