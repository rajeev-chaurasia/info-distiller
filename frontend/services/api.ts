export interface InterestDTO {
    name: string;
    queryTemplate: string;
}

export interface Interest extends InterestDTO {
    id: number;
    userId: number;
}

const API_BASE_URL = 'http://localhost:8082/api';
const API_KEY = process.env.NEXT_PUBLIC_API_KEY || '';

// Function to fetch all interests
export const fetchInterests = async (): Promise<Interest[]> => {
    const response = await fetch(`${API_BASE_URL}/interests`, {
        headers: {'X-API-KEY': API_KEY},
    });
    if (!response.ok) throw new Error('Failed to fetch interests');
    return response.json();
};

// Function to create a new interest
export const createInterest = async (interestData: InterestDTO): Promise<Interest> => {
    const response = await fetch(`${API_BASE_URL}/interests`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json', 'X-API-KEY': API_KEY},
        body: JSON.stringify(interestData),
    });
    if (!response.ok) throw new Error('Failed to create interest');
    return response.json();
};

// Function to update existing interest
export const updateInterest = async (id: number, interestData: InterestDTO): Promise<Interest> => {
    const response = await fetch(`${API_BASE_URL}/interests/${id}`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json', 'X-API-KEY': API_KEY},
        body: JSON.stringify(interestData),
    });
    if (!response.ok) throw new Error('Failed to update interest');
    return response.json();
};

// Function to delete existing interest
export const deleteInterest = async (id: number): Promise<void> => {
    const response = await fetch(`${API_BASE_URL}/interests/${id}`, {
        method: 'DELETE',
        headers: {'X-API-KEY': API_KEY},
    });
    if (!response.ok) throw new Error('Failed to delete interest');
};