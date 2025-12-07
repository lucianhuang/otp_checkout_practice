import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom'; 
import { Button } from "./ui/button"; // 修改處：改成相對路徑
// 原本應該是：import { Button } from "@/components/ui/button"
import { Search, Menu, X } from 'lucide-react';
import { useAuth } from '../hooks/useAuth';

function Header({ showSearchBar = false }) {
  const { isLoggedIn, userName, logout } = useAuth();
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const navigate = useNavigate();

  const toggleMenu = () => setIsMenuOpen(!isMenuOpen);
  const handleAuthClick = () => {
    setIsMenuOpen(false);
    if (isLoggedIn) { logout(); navigate("/"); } else { navigate("/login"); }
  };

  const primaryNavLinks = (
    <>
      <Link to="/events" className="hover:underline py-2 md:py-0" onClick={() => setIsMenuOpen(false)}>活動資訊</Link>
      <Link to="/news" className="hover:underline py-2 md:py-0" onClick={() => setIsMenuOpen(false)}>最新消息</Link>
    </>
  );

  return (
    <header className="sticky top-0 z-50 bg-white text-gray-900 shadow-md font-sans">
      <div className="flex justify-between items-center max-w-7xl mx-auto px-6 py-3">
        <div className="flex items-center space-x-6">
          <Link to="/" className="text-2xl font-extrabold text-primary">OpenTicket</Link>
          <nav className="hidden md:flex items-center space-x-6">{primaryNavLinks}</nav>
        </div>

        <div className="hidden md:flex items-center space-x-4">
          {isLoggedIn ? (
            <Link to="/member/info" className="font-semibold">{userName}</Link> 
          ) : null}
          <Button variant="outline" className="ml-2" onClick={handleAuthClick}>
            <span>{isLoggedIn ? '登出' : '登入/註冊'}</span>
          </Button>
        </div>

        <button className="md:hidden p-2" onClick={toggleMenu}>
          <Menu size={28} className="text-primary" />
        </button>
      </div>
      {/* 手機版選單省略，本機測試用不到那麼細 */}
    </header>
  );
}

export default Header;