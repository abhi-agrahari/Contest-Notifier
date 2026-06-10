const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const fetchContests = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/contests`);
    if (!response.ok) throw new Error('Could not fetch contests from the server.');
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

// You can add more API calls here later:
export const fetchUserPreferences = async () => { /* ... */ }
