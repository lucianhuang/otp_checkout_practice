import { Routes, Route } from 'react-router-dom';
// import Header from './components/Header'; 
// import Footer from './components/Footer'; 

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// 引入頁面
import CheckoutPage from './pages/CheckoutPage'; 
import SuccessPage from './pages/SuccessPage'; 

function App() {
  return (
    <div className="app-container">
      <ToastContainer 
        position="top-center" 
        autoClose={3000} 
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />

      <Routes>
        {/* <Route path="/" element={<HomePage />} /> */}
        <Route path="/checkout/:reservationId" element={<CheckoutPage />} />
        
        {/* 當網址是 /success 時，請顯示 SuccessPage 畫面 */}
        <Route path="/success" element={<SuccessPage />} />
      </Routes>
      
    </div>
  );
}

export default App;