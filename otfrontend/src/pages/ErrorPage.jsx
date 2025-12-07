import { Link } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';

const ErrorPage = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-grow flex items-center justify-center bg-gray-100 p-4">
        <div className="bg-white p-10 rounded-lg shadow-lg text-center max-w-md w-full">
          <h1 className="text-2xl font-bold text-gray-800 mb-2">訂單異常</h1>
          <p className="text-red-500 mb-8">庫存不足，或您重複送出了訂單。</p>
          
          <Link to="/" className="block w-full py-3 rounded bg-primary text-white font-bold hover:bg-orange-600 transition shadow-md">
            返回
          </Link>
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default ErrorPage;