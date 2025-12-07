import { useState, useEffect, useCallback } from 'react';

const BASE_URL = 'http://localhost:8080';

const actualApi = {
    login: async (account, password) => {
        const url = `${BASE_URL}/member/login`; 
        try {
            const res = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ account, password }),
                credentials: "include" 
            });
            const data = await res.json();
            if (res.ok) {
                const userName = data.name || data.user?.name || '會員'; 
                return { success: true, name: userName, message: data.message };
            } else {
                return { success: false, message: data.message };
            }
        } catch (error) {
            console.error('Login API error:', error);
            return { success: false, message: '連線錯誤' };
        }
    },
    getProfile: async () => {
        const url = `${BASE_URL}/member/info`; 
        try {
            const res = await fetch(url, { method: 'GET', credentials: "include" });
            if (res.ok) {
                const data = await res.json();
                const userName = data.name || data.user?.name || '會員'; 
                return { success: true, name: userName };
            } else {
                return { success: false };
            }
        } catch (error) {
            return { success: false };
        }
    },
    logout: async () => {
        const url = `${BASE_URL}/member/logout`; 
        try { await fetch(url, { method: 'POST', credentials: "include" }); } catch (e) {}
    }
};

export const useAuth = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userName, setUserName] = useState("");

    const login = useCallback(async (account, password) => {
        const result = await actualApi.login(account, password);
        if (result.success) {
            setUserName(result.name);
            setIsLoggedIn(true);
            return { success: true };
        } else {
            return { success: false, error: result.message };
        }
    }, []);

    const logout = useCallback(async () => {
        await actualApi.logout(); 
        setUserName("");
        setIsLoggedIn(false);
    }, []);

    useEffect(() => {
        const checkLoginStatus = async () => {
            const result = await actualApi.getProfile();
            if (result.success) {
                setUserName(result.name);
                setIsLoggedIn(true);
            } else {
                setUserName("");
                setIsLoggedIn(false);
            }
        };
        checkLoginStatus();
    }, [logout]); 

    return { isLoggedIn, userName, login, logout };
};