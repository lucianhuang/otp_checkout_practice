import { BrowserRouter, Routes, Route } from 'react-router-dom';

// 引入頁面
import CheckoutPage from './pages/CheckoutPage';
import SuccessPage from './pages/SuccessPage';
import ErrorPage from './pages/ErrorPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 預設首頁 */}
        <Route path="/" element={
          <div className="p-10 text-center">
            <h1 className="text-2xl font-bold mb-4">前端測試入口</h1>
            <a href="/checkout/10" className="text-blue-500 underline text-xl">
               點我前往測試結帳頁 (ID=10)
            </a>
          </div>
        } />

        {/* 三個頁面 */}
        <Route path="/checkout/:reservationId" element={<CheckoutPage />} />
        <Route path="/success" element={<SuccessPage />} />
        <Route path="/error" element={<ErrorPage />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;