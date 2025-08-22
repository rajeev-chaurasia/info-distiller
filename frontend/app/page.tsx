'use client';

import React, { useState } from 'react';

export default function Home() {
  const [url, setUrl] = useState('');
  const [summary, setSummary] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Reset state for a new request
    setIsLoading(true);
    setSummary(null);
    setError('');

    console.log(`[Frontend] Sending request to gateway for URL: ${url}`);

    try {
      const response = await fetch('http://localhost:8082/api/summarize-url', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-API-KEY': process.env.NEXT_PUBLIC_API_KEY || '',
        },
        body: JSON.stringify({ url }),
      });

      const data = await response.json();

      // Handle errors returned from the backend
      if (!response.ok) {
        const errorMsg = data.error || `HTTP error! status: ${response.status}`;
        throw new Error(errorMsg);
      }

      console.log("[Frontend] Received data from backend:", data);

      // Set the summary state with the response from the AI
      if (data && typeof data.summary === 'string') {
        setSummary(data.summary);
      } else {
        throw new Error("Received an invalid summary format from the server.");
      }

    } catch (e: any) {
      console.error("[Frontend] Failed to fetch summary:", e);
      setError(e.message || 'An unknown error occurred. Please check the console.');
    } finally {
      // Ensure loading is turned off, whether the request succeeded or failed
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-4 sm:p-6 lg:p-8">
      {/* Header Section */}
      <header className="text-center my-8">
        <h1 className="text-4xl sm:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-400 to-teal-300">
          Info-Distiller
        </h1>
        <p className="mt-2 text-lg text-slate-400">
          Your personal AI-powered article summarizer.
        </p>
      </header>

      {/* Form Section */}
      <main>
        <form onSubmit={handleSubmit} className="flex flex-col sm:flex-row gap-2">
          <input
            type="url"
            value={url}
            onChange={(e) => setUrl(e.target.value)}
            placeholder="Enter article URL here..."
            className="flex-grow p-3 bg-slate-800 border border-slate-700 rounded-md focus:ring-2 focus:ring-blue-500 focus:outline-none transition-shadow"
            required
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={isLoading || !url}
            className="bg-blue-600 hover:bg-blue-700 disabled:bg-slate-500 disabled:cursor-not-allowed text-white font-bold py-3 px-6 rounded-md transition-colors"
          >
            {isLoading ? 'Distilling...' : 'Summarize'}
          </button>
        </form>
      </main>

      {/* Results Section */}
      <section className="mt-10 p-6 bg-slate-800 border border-slate-700 rounded-md min-h-[200px]">
        <h2 className="text-2xl font-bold mb-4 text-slate-300">Summary</h2>
        {isLoading && <p className="text-slate-400">Generating summary, please wait...</p>}
        {error && <p className="text-red-400">Error: {error}</p>}
        {summary && (
          // The whitespace-pre-wrap class will handle newlines correctly from the AI's response
          <p className="text-slate-300 whitespace-pre-wrap">
            {summary}
          </p>
        )}
      </section>
    </div>
  );
}