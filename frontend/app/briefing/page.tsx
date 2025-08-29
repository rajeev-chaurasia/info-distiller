'use client';

import { useState, useEffect } from 'react';
import { fetchTodaysBriefing, Selection } from '@/services/api';
import withAuth from '@/components/withAuth'

function BriefingPage() {
  const [selections, setSelections] = useState<Selection[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadBriefing = async () => {
      try {
        const data = await fetchTodaysBriefing();
        setSelections(data);
      } catch (err) {
        setError('Failed to load your briefing. Please run the daily job first.');
        console.error(err);
      } finally {
        setIsLoading(false);
      }
    };

    loadBriefing();
  }, []);

  return (
    <div className="max-w-4xl mx-auto p-4 sm:p-6 lg:p-8">
      <header className="my-8">
        <h1 className="text-4xl font-bold text-slate-200">Your Daily Briefing</h1>
        <p className="mt-2 text-lg text-slate-400">
          Here are the top stories for your interests, summarized by AI.
        </p>
      </header>

      <main className="space-y-6">
        {isLoading && <p className="text-slate-400">Loading your briefing...</p>}
        {error && <p className="text-red-400">{error}</p>}

        {!isLoading && !error && selections.length === 0 && (
          <div className="p-6 bg-slate-800 border border-slate-700 rounded-md">
            <p className="text-slate-400">
              Your briefing for today is empty.
            </p>
            <p className="text-slate-500 mt-2">
              You can run the daily job manually by opening a new terminal and running this command from your project's root folder:
            </p>
            <code className="block bg-slate-900 p-2 rounded-md text-sm text-slate-300 mt-2">
              docker compose exec agent-service python -m app.daily_job
            </code>
          </div>
        )}

        {selections.map((selection) => (
          <div key={selection.id} className="p-6 bg-slate-800 border border-slate-700 rounded-md">
            <h2 className="text-2xl font-bold mb-4 text-blue-400">
              <a href={selection.articleUrl} target="_blank" rel="noopener noreferrer" className="hover:underline">
                {selection.articleTitle}
              </a>
            </h2>

            <ul className="list-disc list-inside space-y-2 text-slate-300">
              {selection.summary
                .split('* ')
                .filter(point => point.trim() !== '')
                .map((point, index) => (
                  <li key={index}>{point.trim()}</li>
                ))
              }
            </ul>
          </div>
        ))}
      </main>
    </div>
  );
}

export default withAuth(BriefingPage);