'use client';

import { useState, useEffect } from 'react';
import { fetchInterests, createInterest, updateInterest, deleteInterest, Interest, InterestDTO, generateInterestQuery } from '@/services/api';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';
import axios from 'axios';

export default function InterestsPage() {
  const [interests, setInterests] = useState<Interest[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false); // For form submission
  const [error, setError] = useState('');
  const [topicName, setTopicName] = useState(''); // Only one input field now

  const { isLoggedIn, isLoading: isAuthLoading, logout } = useAuth();
  const router = useRouter();

  // Effect to load initial interests
  useEffect(() => {
    if (!isAuthLoading) {
      if (isLoggedIn) {
        const loadInterests = async () => {
          try {
            const response = await fetchInterests();
            setInterests(response.data);
          } catch (err) {
            if (axios.isAxiosError(err) && (err.response?.status === 401 || err.response?.status === 403)) {
              logout();
            } else {
              setError('Failed to load interests.');
            }
          } finally {
            setIsLoading(false);
          }
        };
        loadInterests();
      } else {
        router.push('/login');
      }
    }
  }, [isLoggedIn, isAuthLoading, router, logout]);

  // Handler for deleting an interest
  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this interest?')) {
      try {
        await deleteInterest(id);
        setInterests(interests.filter(i => i.id !== id));
      } catch (err) {
        setError('Failed to delete interest.');
      }
    }
  };

  // New handler for adding an interest using AI
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError('');

    try {
      // Step 1: Call the AI to generate a query from the simple topic
      console.log(`Generating query for topic: ${topicName}`);
      const queryResponse = await generateInterestQuery(topicName);
      const generatedQuery = queryResponse.query;
      console.log(`AI Generated Query: ${generatedQuery}`);

      // Step 2: Create the new interest object with the generated query
      const interestData: InterestDTO = {
        name: topicName,
        queryTemplate: generatedQuery
      };

      // Step 3: Save the new interest to the database
      const createResponse = await createInterest(interestData);
      setInterests([...interests, createResponse.data]);
      setTopicName('');

    } catch (err) {
      setError('Failed to add new topic. Please try again.');
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading || isAuthLoading) {
    return <p className="text-center mt-10 text-slate-400">Loading interests...</p>;
  }

  return (
    <div className="max-w-4xl mx-auto p-4 sm:p-6 lg:p-8">
      <header className="my-8">
        <h1 className="text-4xl font-bold text-slate-200">Manage Interests</h1>
        <p className="mt-2 text-slate-400">Add a topic, and our AI will create the best search query to find relevant news for your briefing.</p>
      </header>

      <main className="space-y-8">
        <div className="p-6 bg-slate-800 border border-slate-700 rounded-md">
          <h2 className="text-2xl font-bold mb-4 text-slate-300">Add New Topic</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-slate-400">Topic Name (e.g., "AI Startups", "Climate Tech", "Bay Area Housing")</label>
              <input
                id="name"
                type="text"
                value={topicName}
                onChange={(e) => setTopicName(e.target.value)}
                required
                className="mt-1 block w-full p-2 bg-slate-700 border border-slate-600 rounded-md text-white"
              />
            </div>
            <div className="flex gap-2">
              <button type="submit" disabled={isSubmitting} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-md disabled:bg-slate-500">
                {isSubmitting ? 'Generating & Adding...' : 'Add Topic'}
              </button>
            </div>
          </form>
        </div>

        <div className="p-6 bg-slate-800 border border-slate-700 rounded-md">
          <h2 className="text-2xl font-bold mb-4 text-slate-300">Your Topics</h2>
          {error && <p className="text-red-400 my-4">{error}</p>}
          <ul>
            {interests.length > 0 ? interests.map((interest) => (
              <li key={interest.id} className="flex justify-between items-center py-3 border-b border-slate-700 last:border-b-0">
                <div>
                  <p className="font-semibold text-slate-200">{interest.name}</p>
                  <p className="text-sm text-slate-400 italic">Query: {interest.queryTemplate}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleDelete(interest.id)} className="px-3 py-1 text-sm bg-red-600 hover:bg-red-700 rounded">Delete</button>
                </div>
              </li>
            )) : <p className="text-slate-400">You haven't added any topics yet.</p>}
          </ul>
        </div>
      </main>
    </div>
  );
}