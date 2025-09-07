'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { fetchTodaysBriefing, generateBriefing, Selection } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import axios from 'axios';

export default function BriefingPage() {
  const [groupedSelections, setGroupedSelections] = useState<Record<string, Selection[]>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationMessage, setGenerationMessage] = useState('');

  const router = useRouter();
  const { isLoggedIn, logout, isLoading: isAuthLoading } = useAuth();

  useEffect(() => {
    if (!isAuthLoading) {
      if (isLoggedIn) {
        const loadBriefing = async () => {
          try {
            const response = await fetchTodaysBriefing();
            setGroupedSelections(response.data);
          } catch (err) {
            if (axios.isAxiosError(err) && (err.response?.status === 401 || err.response?.status === 403)) {
              logout();
            } else {
              setError('Failed to load briefing.');
            }
          } finally {
            setIsLoading(false);
          }
        };
        loadBriefing();
      } else {
        router.push('/login');
      }
    }
  }, [isLoggedIn, isAuthLoading, router, logout]);

  const handleGenerateBriefing = async () => {
      setIsGenerating(true);
      setError('');
      setGenerationMessage('');
      try {
          const response = await generateBriefing();
          setGenerationMessage(response.data.message + " Please check your email for a notification, then refresh this page.");
      } catch (err) {
          setError('Failed to start the briefing generation process.');
      } finally {
          setIsGenerating(false);
      }
  };

  if (isLoading || isAuthLoading) {
    return <p className="text-center mt-10 text-slate-400">Loading your briefing...</p>;
  }

  return (
    <div className="max-w-4xl mx-auto p-4 sm:p-6 lg:p-8">
      <header className="my-8">
        <h1 className="text-4xl font-bold text-slate-200">Your Daily Briefing</h1>
        <p className="mt-2 text-lg text-slate-400">
          Here are the top stories for your interests, summarized by AI.
        </p>
      </header>

      <main className="space-y-8">
        {error && <p className="text-red-400">{error}</p>}

        {/* --- NEW RENDERING LOGIC --- */}

        {Object.keys(groupedSelections).length > 0 ? (
          // If we have data, map over the interest names (the keys of the object)
          Object.keys(groupedSelections).map(interestName => (
            <section key={interestName} className="p-6 bg-slate-800 border border-slate-700 rounded-md">
              <h2 className="text-3xl font-bold text-slate-300 mb-4 capitalize">{interestName}</h2>
              <div className="space-y-6">
                {/* For each interest, map over its list of articles */}
                {groupedSelections[interestName].map(selection => (
                  <div key={selection.id} className="border-t border-slate-700 pt-4 first:pt-0 first:border-t-0">
                    <h3 className="text-xl font-semibold text-blue-400">
                      <a href={selection.articleUrl} target="_blank" rel="noopener noreferrer" className="hover:underline">
                        {selection.articleTitle}
                      </a>
                    </h3>
                    <ul className="list-disc list-inside space-y-2 text-slate-300 mt-2">
                      {selection.summary
                        .split(/\*\s+/)
                        .filter(point => point.trim() !== '')
                        .map((point, index) => (
                          <li key={index}>{point.trim()}</li>
                        ))
                      }
                    </ul>
                  </div>
                ))}
              </div>
            </section>
          ))
        ) : (
          // If there's no data, show the "Generate" button
          <div className="p-6 bg-slate-800 border border-slate-700 rounded-md text-center">
            <h2 className="text-2xl font-bold text-slate-300">Your briefing for today is not ready yet.</h2>
            <button
              onClick={handleGenerateBriefing}
              disabled={isGenerating}
              className="mt-4 px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-bold rounded-md disabled:bg-slate-500"
            >
              {isGenerating ? 'Processing...' : 'Generate My Briefing Now'}
            </button>
            {generationMessage && <p className="mt-4 text-green-400">{generationMessage}</p>}
          </div>
        )}

      </main>
    </div>
  );
}