import { Link as RouterLink } from "react-router-dom"; 

function Footer() {
  const footerLinks = (
    <div className="space-x-4">
      <RouterLink to="/FormPage" className="hover:underline">聯絡我們</RouterLink>
      <RouterLink to="/FAQList" className="hover:underline">常見問題</RouterLink>
      <RouterLink to="/Privacy" className="hover:underline">Privacy</RouterLink>
    </div>
  );

  return (
    <footer className="bg-gray-900 text-white text-sm p-4 flex items-center justify-between font-sans">
      <div className="flex flex-col md:flex-row items-center space-y-1 md:space-y-0 md:space-x-6 w-full md:w-auto justify-center md:justify-start">
        <RouterLink to="/" className="text-sm font-bold text-primary">OpenTicket</RouterLink>
        <p>© 2025 OpenTicket. All rights reserved.</p>
      </div>
      <div className="hidden md:block">{footerLinks}</div>
    </footer>
  );
}

export default Footer;