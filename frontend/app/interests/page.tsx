'use client';

import { useState, useEffect } from 'react';
import { fetchInterests, createInterest, updateInterest, deleteInterest, Interest, InterestDTO } from '@/services/api';

export default function InterestsPage() {
  const [interests, setInterests] = useState<Interest[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [name, setName] = useState('');
  const [query, setQuery] = useState('');
  const [editingInterest, setEditingInterest] = useState<Interest | null>(null);

  // Initial data load
  useEffect(() => {
    const loadInterests = async () => {
      try {
        setIsLoading(true);
        const data = await fetchInterests();
        setInterests(data);
      } catch (err) {
        setError('Failed to load interests.');
      } finally {
        setIsLoading(false);
      }
    };
    loadInterests();
  }, []);

  // Handler for starting an edit
  const handleEdit = (interest: Interest) => {
    setEditingInterest(interest);
    setName(interest.name);
    setQuery(interest.queryTemplate);
  };

  // Handler for canceling an edit
  const handleCancelEdit = () => {
    setEditingInterest(null);
    setName('');
    setQuery('');
  };

  // Handler for deleting an interest
  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this interest?')) {
      try {
        await deleteInterest(id);
        // Optimistic UI update: filter out the deleted interest
        setInterests(interests.filter(i => i.id !== id));
      } catch (err) {
        setError('Failed to delete interest.');
      }
    }
  };

  // Combined handler for Create and Update
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const interestData: InterestDTO = { name, queryTemplate: query };

    try {
      if (editingInterest) {
        // Update logic
        const updated = await updateInterest(editingInterest.id, interestData);
        setInterests(interests.map(i => (i.id === updated.id ? updated : i)));
      } else {
        // Create logic
        const created = await createInterest(interestData);
        setInterests([...interests, created]);
      }
      handleCancelEdit(); // Reset form
    } catch (err) {
      setError(editingInterest ? 'Failed to update interest.' : 'Failed to add interest.');
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-4 sm:p-6 lg:p-8">
      <header className="my-8">
        <h1 className="text-4xl font-bold text-slate-200">Manage Interests</h1>
      </header>

      <main className="space-y-8">
        <div className="p-6 bg-slate-800 border border-slate-700 rounded-md">
          <h2 className="text-2xl font-bold mb-4 text-slate-300">
            {editingInterest ? 'Edit Topic' : 'Add New Topic'}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-slate-400">Topic Name</label>
              <input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} required className="mt-1 block w-full p-2 bg-slate-700 border border-slate-600 rounded-md" />
            </div>
            <div>
              <label htmlFor="query" className="block text-sm font-medium text-slate-400">Search Query</label>
              <input id="query" type="text" value={query} onChange={(e) => setQuery(e.target.value)} required className="mt-1 block w-full p-2 bg-slate-700 border border-slate-600 rounded-md" />
            </div>
            <div className="flex gap-2">
              <button type="submit" className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-md">
                {editingInterest ? 'Update Interest' : 'Add Interest'}
              </button>
              {editingInterest && (
                <button type="button" onClick={handleCancelEdit} className="px-4 py-2 bg-slate-600 hover:bg-slate-700 text-white font-bold rounded-md">
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        <div className="p-6 bg-slate-800 border border-slate-700 rounded-md">
          <h2 className="text-2xl font-bold mb-4 text-slate-300">Your Topics</h2>
          {isLoading && <p className="text-slate-400">Loading...</p>}
          {error && <p className="text-red-400">{error}</p>}
          <ul>
            {interests.map((interest) => (
              <li key={interest.id} className="flex justify-between items-center py-2 border-b border-slate-700 last:border-b-0">
                <div>
                  <p className="font-semibold text-slate-200">{interest.name}</p>
                  <p className="text-sm text-slate-400">{interest.queryTemplate}</p>
                </div>
                <div className="flex gap-2">
                  <button onClick={() => handleEdit(interest)} className="px-3 py-1 text-sm bg-yellow-600 hover:bg-yellow-700 rounded">Edit</button>
                  <button onClick={() => handleDelete(interest.id)} className="px-3 py-1 text-sm bg-red-600 hover:bg-red-700 rounded">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </main>
    </div>
  );
}