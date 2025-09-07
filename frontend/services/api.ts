import axios from 'axios';

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

// --- AXIOS INSTANCE ---
export const api = axios.create({
  baseURL: 'http://localhost:8082/api'
});

// --- INTERCEPTOR FOR GLOBAL ERROR HANDLING ---
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.log('Authentication error, logging out...');
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// --- AUTHENTICATION API ---
export const generateOtp = (email: string) => api.post('/auth/generate-otp', { email });
export const validateOtp = (email: string, otp: string) => api.post<{ token: string }>('/auth/validate-otp', { email, otp });

// --- PROTECTED APIs ---
export const fetchInterests = () => api.get<Interest[]>('/interests');
export const createInterest = (interestData: InterestDTO) => api.post<Interest>('/interests', interestData);
export const generateInterestQuery = (topic: string) => api.post<{ query: string }>('/interests/generate-query', { topic });
export const deleteInterest = (id: number) => api.delete(`/interests/${id}`);
export const fetchTodaysBriefing = () => api.get<Record<string, Selection[]>>('/briefing/today');
export const generateBriefing = () => api.post<{ message: string }>('/briefing/generate');
