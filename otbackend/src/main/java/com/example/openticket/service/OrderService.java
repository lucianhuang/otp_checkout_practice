// 宣告檔案位置 (Package)
package com.example.openticket.service;

// List 不是 Java的關鍵字，是一個「介面」，用來操作「集合資料」
import java.util.List;
import java.time.LocalDateTime; // 記得引入這個
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
// 引入 Spring 的 Service 功能
import org.springframework.stereotype.Service;
// 交易控制
import org.springframework.transaction.annotation.Transactional; 

import com.example.openticket.dto.CheckoutRequest;
import com.example.openticket.entity.*;
import com.example.openticket.repository.*;

@Service
// Spring 會在記憶體中執行 new OrderService()
public class OrderService {

    // 依賴注入 (@Autowired)，去找各個 Repository (倉庫管理員)

    // 管訂單表的
    @Autowired
    private OrderRepository orderRepository;

    // 管明細表的
    @Autowired
    private CheckoutOrderRepository checkoutOrderRepository; 
    
    // 管付款表的
    @Autowired
    private PaymentRepository paymentRepository;      
    
    // 管預約表的
    @Autowired
    private ReservationRepository reservationRepository; 
    
    // 管票種庫存的 (最重要!)
    @Autowired
    private EventTicketTypeRepository eventTicketTypeRepository; 
    
    // 管預約明細的
    @Autowired
    private ReservationItemRepository reservationItemRepository; 

    // 輸入：CheckoutRequest (點餐單)
    // 輸出：Long (做好的訂單 ID)
    // 加上 @Transactional
    // 如果中間任何一行報錯 (例如庫存不足)，資料庫會自動「回滾 (Rollback)」，
    // 避免「錢付了、訂單沒建」或是「庫存扣了、錢沒收」
    @Transactional
    public Long createOrder(CheckoutRequest request) {

        // 直接反查訂單表 (Idempotency Check)
        // 邏輯：為了不依賴資料庫管理員加欄位，直接查 orders 表
        // 動作：如果這裡回傳 true，代表這張預約單已經被用過了，直接擋掉
        if (orderRepository.existsByReservationsId(request.getReservationId())) {
            throw new RuntimeException("這筆預約單已經結帳過了，請勿重複送出！");
        }

        // 找預約單
        // 動作：拿預約單上的 reservationId，叫管理員去資料庫查這張預約單還在不在。
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("找不到預約單")); 

        // 檢查時效 (Security Check) - 修改版
        // AI Code Review 建議：
        // 不修改資料庫 (不依賴 expiresAt 欄位)，直接用建立時間 + 10秒 來判斷
        // 假設 Reservation Entity 有 getCreatedAt() 方法 (通常 entity 都會有建立時間)
        LocalDateTime deadLine = reservation.getCreatedAt().plusSeconds(10); // 這裡設定 10 秒
        
        if (LocalDateTime.now().isAfter(deadLine)) {
            // 如果現在時間 晚於 (建立時間+10秒)，就報錯
            throw new RuntimeException("結帳逾時 (超過10秒)，請重新預約！");
        }

        // 把「建立主訂單」的順序往前移
        // 原因：需要先拿到 Order ID，等一下在跑迴圈時，才能直接把明細存進去
        
        // 建立主訂單
        Order order = new Order(); // 拿出一張新的空白訂單紙
        
        // 把資料填進去
        order.setReservationsId(reservation.getId());
        order.setInvoiceType(request.getInvoiceType());   // 從 DTO 抄過來的
        order.setInvoiceTaxId(request.getInvoiceTaxId()); // 從 DTO 抄過來的
        order.setStatus("PENDING"); // 狀態標注：待付款
        
        // 存檔回去，這時候資料庫會自動產生一個 ID
        orderRepository.save(order);

        // 把「扣庫存」與「存明細」的迴圈合併 (提升效能)
        
        // 動作：先去查這張預約單裡，到底買了哪些票？
        List<ReservationItem> items = reservationItemRepository.findByReservationsId(request.getReservationId());
        
        // 跑迴圈～：一筆一筆處理
        for (ReservationItem item : items) {
            
            // 使用悲觀鎖 (Pessimistic Lock)
            // 先呼叫在 Repository 準備好的 findWithLockById
            // 資料庫會對這行資料上鎖 (SELECT ... FOR UPDATE)
            EventTicketType ticketType = eventTicketTypeRepository.findWithLockById(item.getEventTicketTypeId())
                    .orElseThrow(() -> new RuntimeException("找不到票種"));
            
            // 檢查庫存夠不夠
            if (ticketType.getCustomLimit() < item.getQuantity()) {
                throw new RuntimeException("來晚一步！票被搶光了 (庫存不足)");
            }
            
            // 動作：拿出現在的庫存量，減掉要買的數量
            // 動作：寫回資料庫
            ticketType.setCustomLimit(ticketType.getCustomLimit() - item.getQuantity());
            eventTicketTypeRepository.save(ticketType);

            // 在這裡順便把明細存進去 (不用跑兩次迴圈)
            CheckoutOrder checkoutOrder = new CheckoutOrder();
            checkoutOrder.setOrder(order); 
            checkoutOrder.setEventTicketTypeId(item.getEventTicketTypeId());
            checkoutOrder.setQuantity(item.getQuantity());
            
            // 不再寫死 100 元，改從資料庫抓真實價格
            // (需確認 EventTicketType Entity 有 customPrice 欄位)
            if (ticketType.getCustomPrice() != null) {
                checkoutOrder.setUnitPrice(ticketType.getCustomPrice());
            } else {
                checkoutOrder.setUnitPrice(new BigDecimal(0)); // 防呆
            }
            
            checkoutOrderRepository.save(checkoutOrder);
        }

        // 建立付款單，紀錄要收多少錢
        Payment payment = new Payment();
        
        // 把這張付款單跟訂單綁在一起 (Foreign Key)
        payment.setOrder(order); 

        // 寫死線上支付
        payment.setPaymentMethod("ONLINE_PAY"); 
        
        // 金額從預約單抄過來
        payment.setAmount(reservation.getTotalAmount()); 
       
        // 狀態蓋章：待付款
        payment.setStatus("PENDING");
        
        // 存檔
        paymentRepository.save(payment); 

        // [AI 移除說明]：原本這裡有「核銷預約單 (reservation.setStatus("ORDERED"))」
        // 原因：
        // 資料庫真實 Schema 中沒有 status 欄位，不去改修改 DB。
        // 改用最上面的 existsByReservationsId 來檢查是否重複。
        // 所以這段更新狀態的程式碼被移除了。

        System.out.println("訂單建立成功 (V3 Optimized)，ID: " + order.getId());
        return order.getId(); 
    }
}