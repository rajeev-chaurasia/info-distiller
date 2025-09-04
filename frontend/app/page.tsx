'use client';

import Link from 'next/link';
import {useAuth} from '../context/AuthContext';

export default function HomePage() {
    const {isLoggedIn} = useAuth();
    return (
        <div className="flex flex-col items-center justify-center min-h-screen text-center p-4">
            <h1 className="text-5xl font-extrabold text-slate-200">
                Welcome to Info-Distiller
            </h1>
            <p className="mt-4 text-xl text-slate-400">
                Your personal AI-powered daily news briefing. We cut through the noise to bring you what matters.
            </p>

            <Link
                href={isLoggedIn ? "/briefing" : "/login"} // Dynamic link!
                className="mt-8 px-6 py-3 bg-blue-600 text-white font-bold rounded-lg hover:bg-blue-700 transition-colors">
                Get Started
            </Link>
        </div>
    );
}