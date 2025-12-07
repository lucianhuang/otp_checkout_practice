import { useState, useEffect } from 'react'; // 引入 useEffect
import { useParams, useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
// 引入 Toast 套件
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const CheckoutPage = () => {
  // 從網址抓取預約單號 (例如 /checkout/10)
  const { reservationId } = useParams();
  const navigate = useNavigate();

  // 表單狀態管理
  const [invoiceType, setInvoiceType] = useState('PERSONAL'); // PERSONAL 或 COMPANY
  const [taxId, setTaxId] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 改用 State 存資料，原本是寫死的 orderItems
  const [loading, setLoading] = useState(true);
  const [orderData, setOrderData] = useState(null); // 用來存後端抓回來的資料

  // 進入頁面後，去後端抓取真實訂單資料
  useEffect(() => {
    const fetchReservation = async () => {
      try {
        // 呼叫查詢 API (GET)
        const res = await fetch(`http://localhost:8080/api/reservations/${reservationId}/checkout`);
        // 注意：這裡我幫你把網址改成 /checkout 了，對應你 Controller 的 @GetMapping("/{id}/checkout")
        
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

  // 按下結帳按鈕的邏輯
  const handleCheckout = async () => {
    // 防呆：如果是公司發票，統編必填
    if (invoiceType === 'COMPANY' && taxId.length !== 8) {
      toast.warning('請輸入正確的 8 碼統一編號');
      return;
    }

    setIsSubmitting(true);

    // 準備要傳給後端的 JSON
    const payload = {
      reservationId: parseInt(reservationId), // 轉成數字
      invoiceType: invoiceType,
      invoiceTaxId: invoiceType === 'COMPANY' ? taxId : ''
    };

    try {
      // 修正結帳網址 (要打給 OrderController 的 checkout)
      const response = await fetch('http://localhost:8080/api/orders/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        // 200，則跳轉成功頁
        toast.success("結帳成功！跳轉中...");
        setTimeout(() => {
          navigate('/success');
        }, 1500);
      } else {
        // 500，則跳轉失敗頁
        // 嘗試讀取後端傳回來的錯誤文字 (我們在 Java 寫的 throw Exception 訊息)
        const errorMsg = await response.text(); 
        const showMsg = (errorMsg && errorMsg.length < 100) ? errorMsg : '結帳失敗，請稍後再試';

        console.error('結帳失敗:', showMsg);
        toast.error(showMsg);
        // navigate('/error'); // 建議不要跳頁，讓使用者看到錯誤訊息比較好
      }
    } catch (error) {
      console.error('網路連線錯誤:', error);
      toast.error('無法連接伺服器，請確認後端有開 (Port 8080)');
    } finally {
      setIsSubmitting(false);
    }
  };

  // 載入中或無資料的防呆顯示
  if (loading) return <div className="min-h-screen flex items-center justify-center text-xl">載入訂單資料中...</div>;
  if (!orderData) return <div className="min-h-screen flex items-center justify-center text-xl text-red-500">查無資料 (請確認資料庫有 ID 為 {reservationId} 的資料)</div>;

  return (
    <div className="flex flex-col min-h-screen">
      {/* 頁首 */}
      <Header />

      {/* 主要內容區 (灰色背景) */}
      <main className="flex-grow bg-gray-100 p-6 md:p-12 font-sans">
        <div className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-12 gap-8">
          
          {/* 左邊：訂單摘要 (佔 5 欄) */}
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

                {/* 改成渲染 orderData (後端資料) --- */}
                {orderData.items && orderData.items.map((item, idx) => (
                  <div key={idx} className="grid grid-cols-5 text-base text-gray-700 py-4 border-b border-gray-100 items-center">
                    {/* 1. 商品名稱 */}
                    <div className="font-medium text-left truncate pr-2">
                      活動
                    </div>
                    {/* 2. 票種 */}
                    <div className="text-gray-500 text-left text-sm">
                      {item.ticketName}
                    </div>
                    {/* 3. 單價 */}
                    <div className="text-center">
                      {item.price}
                    </div>
                    {/* 4. 數量 */}
                    <div className="text-center">
                      x {item.quantity}
                    </div>
                    {/* 5. 小計 */}
                    <div className="text-right font-medium">
                      {item.price * item.quantity}
                    </div>
                  </div>
                ))}
              </div>

              <div className="flex justify-between items-center mt-6 pt-4 border-t border-gray-200">
                <span className="text-gray-600 font-medium text-lg">總價</span>
                <span className="text-3xl font-bold text-primary">
                  {/* 使用後端回傳的總價 */}
                  {orderData.totalAmount} 元
                </span>
              </div>
            </div>
          </div>

          {/* 右邊：填寫資料 (佔 7 欄) */}
          <div className="lg:col-span-7 space-y-6">
            
            {/* 卡片 1: 會員資訊 */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 text-gray-800">會員資訊</h3>
              <div className="text-base text-gray-600 space-y-2">
                {/* 使用後端回傳的會員資料 */}
                <p><span className="text-gray-400 mr-2">會員名稱</span> {orderData.userName}</p>
                <p><span className="text-gray-400 mr-2">電子信箱</span> {orderData.userEmail}</p>
              </div>
            </div>

            {/* 卡片 2: 付款方式 (維持不變) */}
            <div className="bg-white rounded-lg shadow-md p-6">
              <h3 className="text-lg font-bold mb-4 text-gray-800">付款方式</h3>
              <label className="flex items-center space-x-3 p-3 border border-primary/30 bg-primary/5 rounded cursor-pointer">
                <input type="radio" checked readOnly className="text-primary focus:ring-primary w-5 h-5" />
                <span className="font-medium text-gray-700 text-lg">線上支付</span>
              </label>
            </div>

            {/* 卡片 3: 發票設定 (維持不變) */}
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

            {/* 底部按鈕 */}
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
                {isSubmitting ? '處理中...' : '結帳'}
              </button>
            </div>

          </div>
        </div>
      </main>

      {/* 頁尾 */}
      <Footer />
      {/* Toast 容器 */}
      <ToastContainer position="top-center" autoClose={3000} />
    </div>
  );
};

export default CheckoutPage;