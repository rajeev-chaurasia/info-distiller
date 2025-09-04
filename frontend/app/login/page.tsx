'use client';

import React, { useState } from 'react';
import { generateOtp, validateOtp } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [isOtpSent, setIsOtpSent] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const { login } = useAuth();
    const router = useRouter();

    const handleEmailSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        setMessage('');
        try {
            const response = await generateOtp(email);
            setMessage(response.data.message);
            setIsOtpSent(true);
        } catch (err) {
            setError('Failed to send OTP. Please check the email and try again.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleOtpSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        setMessage('');
        try {
            const response = await validateOtp(email, otp);
            const token = response.data.token;

            login(token);
            setMessage('Login successful! Redirecting...');
            setTimeout(() => {
                router.push('/briefing');
            }, 1500);

        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'An unknown error occurred.';
            setError(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    const handleChangeEmail = () => {
        setIsOtpSent(false);
        setMessage('');
        setError('');
        setOtp('');
    };

    return (
        <div className="max-w-md mx-auto p-4 sm:p-6 lg:p-8">
            <header className="my-8 text-center">
                <h1 className="text-4xl font-bold text-slate-200">Login or Register</h1>
                <p className="mt-2 text-lg text-slate-400">
                    Enter your email to receive a secure login code.
                </p>
            </header>

            <main className="p-6 bg-slate-800 border border-slate-700 rounded-md">
                {!isOtpSent ? (
                    <form onSubmit={handleEmailSubmit} className="space-y-4">
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-slate-400">Email Address</label>
                            <input
                                id="email" type="email" value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                placeholder="you@example.com"
                                className="mt-1 block w-full p-2 bg-slate-700 border border-slate-600 rounded-md text-white"
                                required
                            />
                        </div>
                        <button type="submit" disabled={isLoading}
                                className="w-full px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-md disabled:bg-slate-500">
                            {isLoading ? 'Sending...' : 'Send Login Code'}
                        </button>
                    </form>
                ) : (
                    <form onSubmit={handleOtpSubmit} className="space-y-4">
                        <p className="text-sm text-slate-400">
                            A login code was sent to <span className="font-bold text-slate-200">{email}</span>.
                        </p>
                        <div>
                            <label htmlFor="otp" className="block text-sm font-medium text-slate-400">Enter Login Code</label>
                            <input
                                id="otp" type="text" value={otp}
                                onChange={(e) => setOtp(e.target.value)}
                                placeholder="123456"
                                className="mt-1 block w-full p-2 bg-slate-700 border border-slate-600 rounded-md text-white"
                                required
                            />
                        </div>
                        <button type="submit" disabled={isLoading}
                                className="w-full px-4 py-2 bg-green-600 hover:bg-green-700 text-white font-bold rounded-md disabled:bg-slate-500">
                            {isLoading ? 'Verifying...' : 'Login'}
                        </button>
                        <div className="flex justify-between text-sm">
                            <button type="button" onClick={handleChangeEmail}
                                    className="text-slate-400 hover:text-white">Change Email
                            </button>
                            <button type="button" onClick={handleEmailSubmit} disabled={isLoading}
                                    className="text-slate-400 hover:text-white disabled:text-slate-600">
                                {isLoading ? 'Sending...' : 'Resend Code'}
                            </button>
                        </div>
                    </form>
                )}

                {message && <p className="mt-4 text-center text-green-400">{message}</p>}
                {error && <p className="mt-4 text-center text-red-400">{error}</p>}
            </main>
        </div>
    );
}