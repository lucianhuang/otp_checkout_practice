package com.example.openticket.service;

import com.example.openticket.dto.CheckoutRequest;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewebPayService {

    // ==========================================
    // 【重要】金鑰
    // ==========================================
    private final String MERCHANT_ID = "MS157513558"; 
    private final String HASH_KEY    = "3QX2NpDpLOFIurGOI67FXwLSJZU0ivbc";    
    private final String HASH_IV     = "Cy4F4iXdtk1TbJdP";     
    
    // 藍新測試環境網址
    private final String API_URL = "https://ccore.newebpay.com/MPG/mpg_gateway";

    public String genCheckOutForm(CheckoutRequest request, Integer amount, Long orderId) {
        // 準備參數
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", MERCHANT_ID);
        params.put("RespondType", "JSON");
        params.put("TimeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("Version", "2.0");
        params.put("MerchantOrderNo", "OT" + System.currentTimeMillis());
        params.put("Amt", String.valueOf(amount));
        params.put("ItemDesc", "OpenTicket票券");
        
        // 建議這裡可以改成從 request 拿 Email，如果 request 裡沒有就用預設值
        params.put("Email", "test@example.com"); 
        params.put("LoginType", "0");

    
        params.put("ReturnURL", ""); // 自動跳轉 (本次不使用)
        params.put("NotifyURL", ""); // 幕後通知 (本次不使用)
        
        // 設定交易成功後，藍新頁面顯示的「返回商店」按鈕連結
        params.put("ClientBackURL", "http://localhost:5173/success"); 

        // 參數轉字串並 URL Encode
        String dataString = params.entrySet().stream()
                .map(e -> {
                    try {
                        return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString());
                    } catch (Exception ex) {
                        return e.getKey() + "=" + e.getValue();
                    }
                })
                .collect(Collectors.joining("&"));

        // 加密 (AES)
        String tradeInfo = encryptAES(dataString);

        // 計算雜湊 (SHA256)
        String tradeSha = encryptSHA256("HashKey=" + HASH_KEY + "&" + tradeInfo + "&HashIV=" + HASH_IV);

        // 產生 HTML 表單
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h3>正在前往藍新金流付款...</h3>");
        html.append("<form id='newebpayForm' action='").append(API_URL).append("' method='post'>");
        html.append("<input type='hidden' name='MerchantID' value='").append(MERCHANT_ID).append("'>");
        html.append("<input type='hidden' name='TradeInfo' value='").append(tradeInfo).append("'>");
        html.append("<input type='hidden' name='TradeSha' value='").append(tradeSha).append("'>");
        html.append("<input type='hidden' name='Version' value='2.0'>");
        html.append("</form>");
        html.append("<script>document.getElementById('newebpayForm').submit();</script>");
        html.append("</body></html>");

        return html.toString();
    }

    // AES 加密工具
    private String encryptAES(String data) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(HASH_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec iv = new IvParameterSpec(HASH_IV.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            return bytesToHex(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失敗", e);
        }
    }

    // SHA256 加密工具
    private String encryptSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 加密失敗", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}