import { api } from '../context/AuthContext';

// --- TYPE DEFINITIONS ---
export interface InterestDTO {
    name: string;
    queryTemplate: string;
}

export interface Interest extends InterestDTO{
    id: number;
    userId: number;
}

export interface Selection {
    id: number;
    userId: number;
    articleUrl: string;
    articleTitle: string;
    summary: string;
    pickedForDate: string;
}

// --- AUTHENTICATION API ---
export const generateOtp = (email: string) => api.post('/auth/generate-otp', { email });
export const validateOtp = (email: string, otp: string) => api.post('/auth/validate-otp', { email, otp });

// --- PROTECTED APIs ---
export const fetchInterests = () => api.get<Interest[]>('/interests');
export const createInterest = (interestData: InterestDTO) => api.post<Interest>('/interests', interestData);
export const updateInterest = (id: number, interestData: InterestDTO) => api.put<Interest>(`/interests/${id}`, interestData);
export const deleteInterest = (id: number) => api.delete(`/interests/${id}`);
export const fetchTodaysBriefing = () => api.get<Selection[]>('/briefing/today');

export const generateInterestQuery = async (topic: string): Promise<{ query: string }> => {
    const response = await api.post('/interests/generate-query', { topic });
    return response.data;
};

export const generateBriefing = async (): Promise<{ message: string }> => {
    const response = await api.post('/briefing/generate');
    return response.data;
};