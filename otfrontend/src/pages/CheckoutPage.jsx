import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';

// 只引入 toast，不要引入 ToastContainer (App.jsx 已經有了)
import { toast } from 'react-toastify';

const CheckoutPage = () => {
  const { reservationId } = useParams();
  const navigate = useNavigate();

  const [invoiceType, setInvoiceType] = useState('PERSONAL');
  const [taxId, setTaxId] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);
  const [orderData, setOrderData] = useState(null);

  // 載入預約單資料
  useEffect(() => {
    const fetchReservation = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/reservations/${reservationId}/checkout`);
        
        if (res.ok) {
          const data = await res.json();
          console.log("從後端抓到的資料:", data);
          setOrderData(data);
        } else {
          console.error("找不到訂單");
          toast.error("找不到訂單資料");
        }
      } catch (err) {
        console.error("連線失敗", err);
        toast.error("連線失敗，請檢查後端");
      } finally {
        setLoading(false);
      }
    };
    if (reservationId) fetchReservation();
  }, [reservationId]);

  // 處理結帳按鈕點擊
  const handleCheckout = async () => {
    // 公司戶發票檢查
    if (invoiceType === 'COMPANY' && taxId.length !== 8) {
      toast.warning('請輸入正確的 8 碼統一編號');
      return;
    }

    setIsSubmitting(true);

    const payload = {
      reservationId: parseInt(reservationId),
      invoiceType: invoiceType,
      invoiceTaxId: invoiceType === 'COMPANY' ? taxId : ''
    };

    try {
      // 送出訂單給後端
      const response = await fetch('http://localhost:8080/api/orders/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        // --- 處理藍新回傳的 HTML ---
        
        // 取得後端產生的 HTML 表單字串
        const htmlForm = await response.text(); 

        // 創建一個隱藏的 div 來放這個表單
        const div = document.createElement('div');
        div.innerHTML = htmlForm;
        
        // 把這個 div 插入到目前的網頁中
        document.body.appendChild(div);

        // 找到藍新的表單並自動送出 (後端設定 ID 為 newebpayForm)
        const form = document.getElementById('newebpayForm');
        
        if (form) {
          toast.success("訂單建立成功，前往藍新付款...");
          form.submit(); // 自動跳轉
        } else {
          const ecpayForm = document.getElementById('allPayAPIForm');
          if (ecpayForm) {
            toast.success("訂單建立成功，前往綠界付款...");
            ecpayForm.submit();
          } else {
            console.error("找不到付款表單，請檢查後端回傳的 HTML ID");
            toast.error("轉跳失敗，請聯繫管理員");
            setIsSubmitting(false); // 失敗才恢復按鈕
          }
        }
        

      } else {
        
        const errorMsg = await response.text(); 
        const showMsg = (errorMsg && errorMsg.length < 100) ? errorMsg : '結帳失敗，請稍後再試';
        console.error('結帳失敗:', showMsg);
        toast.error(showMsg);
        setIsSubmitting(false);
      }
    } catch (error) {
      console.error('網路連線錯誤:', error);
      toast.error('無法連接伺服器，請確認後端有開 (Port 8080)');
      setIsSubmitting(false);
    } 
  };

  if (loading) return <div className="min-h-screen flex items-center justify-center text-xl">載入訂單資料中...</div>;
  if (!orderData) return <div className="min-h-screen flex items-center justify-center text-xl text-red-500">查無資料 (請確認資料庫有 ID 為 {reservationId} 的資料)</div>;

  return (
    <div className="flex flex-col min-h-screen">
      <Header />

      <main className="flex-grow bg-gray-100 p-6 md:p-12 font-sans">
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
          
          {/* 左邊：訂單摘要 */}
          <div className="lg:col-span-5">
            <div className="bg-white rounded-lg shadow-md p-6 sticky top-24">
              <h2 className="text-xl font-bold mb-6 text-gray-800">訂單摘要</h2>
              
              <div className="space-y-4">
                <div className="grid grid-cols-5 text-sm text-gray-500 border-b pb-2">
                  <div className="text-left">商品</div>
                  <div className="text-left">票種</div>
                  <div className="text-center">單價</div>
                  <div className="text-center">數量</div>
                  <div className="text-right">小計</div>
                </div>

                {orderData.items && orderData.items.map((item, idx) => (
                  <div key={idx} className="grid grid-cols-5 text-base text-gray-700 py-4 border-b border-gray-100 items-center">
                    <div className="font-medium text-left truncate pr-2">活動</div>
                    <div className="text-gray-500 text-left text-sm">{item.ticketName}</div>
                    <div className="text-center">{item.price}</div>
                    <div className="text-center">x {item.quantity}</div>
                    <div className="text-right font-medium">{item.price * item.quantity}</div>
                  </div>
                ))}
              </div>

              <div className="flex justify-between items-center mt-6 pt-4 border-t border-gray-200">
                <span className="text-gray-600 font-medium text-lg">總價</span>
                <span className="text-3xl font-bold text-primary">
                  {orderData.totalAmount} 元
                </span>
              </div>
            </div>
          </div>

          {/* 右邊：填寫資料 */}
          <div className="lg:col-span-7 space-y-6">
            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 text-gray-800">會員資訊</h3>
              <div className="text-base text-gray-600 space-y-2">
                <p><span className="text-gray-400 mr-2">會員名稱</span> {orderData.userName}</p>
                <p><span className="text-gray-400 mr-2">電子信箱</span> {orderData.userEmail}</p>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 text-gray-800">付款方式</h3>
              <label className="flex items-center space-x-3 p-3 border border-primary/30 bg-primary/5 rounded cursor-pointer">
                <input type="radio" checked readOnly className="text-primary focus:ring-primary w-5 h-5" />
                {/* 藍新金流 */}
                <span className="font-medium text-gray-700 text-lg">線上支付 (藍新金流)</span>
              </label>
              <p className="text-sm text-gray-500 mt-2 ml-8">支援信用卡、ATM、WebATM</p>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 text-gray-800">收據</h3>
              <div className="flex flex-col space-y-4">
                <label className="flex items-center space-x-3 cursor-pointer">
                  <input 
                    type="radio" 
                    name="invoice"
                    value="PERSONAL"
                    checked={invoiceType === 'PERSONAL'}
                    onChange={(e) => setInvoiceType(e.target.value)}
                    className="accent-primary w-5 h-5"
                  />
                  <span className="text-lg">個人發票</span>
                </label>

                <div className="flex flex-col">
                  <label className="flex items-center space-x-3 cursor-pointer mb-2">
                    <input 
                      type="radio" 
                      name="invoice"
                      value="COMPANY"
                      checked={invoiceType === 'COMPANY'}
                      onChange={(e) => setInvoiceType(e.target.value)}
                      className="accent-primary w-5 h-5"
                    />
                    <span className="text-lg">統一編號</span>
                  </label>
                  <div className={`overflow-hidden transition-all duration-300 ${invoiceType === 'COMPANY' ? 'max-h-20 opacity-100' : 'max-h-0 opacity-0'}`}>
                    <input 
                      type="text" 
                      placeholder="請輸入 8 碼統一編號"
                      value={taxId}
                      onChange={(e) => setTaxId(e.target.value)}
                      maxLength={8}
                      className="ml-8 w-64 border border-gray-300 rounded px-3 py-2 text-base focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div className="flex justify-between items-center pt-4">
              <button className="px-6 py-3 rounded border border-gray-300 text-gray-600 hover:bg-gray-50 transition text-lg">
                繼續購物
              </button>
              
              <button 
                onClick={handleCheckout}
                disabled={isSubmitting}
                className={`px-10 py-3 rounded text-white font-bold shadow-md transition transform active:scale-95 text-lg
                  ${isSubmitting ? 'bg-gray-400 cursor-not-allowed' : 'bg-primary hover:bg-orange-600'}
                `}
              >
                {isSubmitting ? '處理中...' : '前往付款'}
              </button>
            </div>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default CheckoutPage;