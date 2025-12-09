import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
// 引入 BrowserRouter
import { BrowserRouter } from 'react-router-dom' 

import './index.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    {/* 用 BrowserRouter 包住 App */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>,
)