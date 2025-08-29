export interface InterestDTO {
    name: string;
    queryTemplate: string;
}

export interface Interest extends InterestDTO {
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

const API_BASE_URL = 'http://localhost:8082/api';

// This helper function gets the auth token from localStorage.
const getAuthHeaders = (): HeadersInit => {
    const token = localStorage.getItem('authToken');
    if (!token) {
        throw new Error('Authentication token not found. Please log in.');
    }
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};

// --- AUTHENTICATION API ---

export const generateOtp = async (email: string): Promise<{ message: string }> => {
    const response = await fetch(`${API_BASE_URL}/auth/generate-otp`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
    });
    if (!response.ok) {
        throw new Error('Failed to generate OTP');
    }
    return response.json();
};

export const validateOtp = async (email: string, otp: string): Promise<{ token: string }> => {
    const response = await fetch(`${API_BASE_URL}/auth/validate-otp`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, otp }),
    });
    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Invalid or expired OTP');
    }
    return response.json();
};

// --- PROTECTED APIs (Require JWT) ---

export const fetchInterests = async (): Promise<Interest[]> => {
    const response = await fetch(`${API_BASE_URL}/interests`, {
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to fetch interests');
    return response.json();
};

export const createInterest = async (interestData: InterestDTO): Promise<Interest> => {
    const response = await fetch(`${API_BASE_URL}/interests`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(interestData),
    });
    if (!response.ok) throw new Error('Failed to create interest');
    return response.json();
};

export const updateInterest = async (id: number, interestData: InterestDTO): Promise<Interest> => {
    const response = await fetch(`${API_BASE_URL}/interests/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(interestData),
    });
    if (!response.ok) throw new Error('Failed to update interest');
    return response.json();
};

export const deleteInterest = async (id: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/interests/${id}`, {
        method: 'DELETE',
        headers: getAuthHeaders(),
    });
    if (!response.ok) throw new Error('Failed to delete interest');
};

export const fetchTodaysBriefing = async (): Promise<Selection[]> => {
    const response = await fetch(`${API_BASE_URL}/briefing/today`, {
        headers: getAuthHeaders(),
    });
    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem('authToken');
        throw new Error('Session expired. Please log in again.');
    }
    if (!response.ok) {
        throw new Error('Failed to fetch today\'s briefing');
    }
    return response.json();
};