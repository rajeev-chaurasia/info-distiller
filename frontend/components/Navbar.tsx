'use client';

import Link from 'next/link';
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const router = useRouter();

  useEffect(() => {
    // Check if the auth token exists in local storage
    const token = localStorage.getItem('authToken');
    setIsLoggedIn(!!token);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    setIsLoggedIn(false);
    router.push('/login');
  };

  return (
    <nav className="bg-slate-800 border-b border-slate-700">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <div className="flex items-center">
            <Link href="/frontend/public" className="text-white font-bold text-xl">
              Info-Distiller
            </Link>
          </div>
          <div>
            {isLoggedIn ? (
              <button
                onClick={handleLogout}
                className="px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-700 hover:text-white"
              >
                Logout
              </button>
            ) : (
              <Link href="/login" className="px-3 py-2 rounded-md text-sm font-medium text-slate-300 hover:bg-slate-700 hover:text-white">
                Login
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}