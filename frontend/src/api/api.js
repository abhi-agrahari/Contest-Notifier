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

export const fetchUserPreferences = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/preferences`, {
        // Assuming credentials/cookies are handled by the browser
        // If not using cookies (e.g. JWT in Header), this would need more work.
        // But the backend uses @AuthenticationPrincipal which usually implies session/cookie or configured OAuth2.
        credentials: 'include' 
    });
    if (!response.ok) {
        if (response.status === 401) throw new Error('Unauthorized');
        throw new Error('Failed to fetch preferences');
    }
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const saveUserPreference = async (preference) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/preferences`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(preference),
      credentials: 'include'
    });
    if (!response.ok) throw new Error('Failed to save preference');
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const deleteUserPreference = async (platform) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/preferences/${platform}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    if (!response.ok) throw new Error('Failed to delete preference');
    return true;
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const fetchNotificationSetting = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/preferences/notifications`, {
      credentials: 'include'
    });
    if (!response.ok) throw new Error('Failed to fetch notification setting');
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const updateNotificationSetting = async (enabled) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/preferences/notifications`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(enabled),
      credentials: 'include'
    });
    if (!response.ok) throw new Error('Failed to update notification setting');
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const updateUserHandles = async (handles) => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/user/handles`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(handles),
      credentials: 'include'
    });
    if (!response.ok) throw new Error('Failed to update handles');
    return await response.json();
  } catch (error) {
    console.error('API Error:', error);
    throw error;
  }
};

export const fetchUserProfile = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/user/me`, {
            credentials: 'include'
        });
        if (!response.ok) {
            if (response.status === 401) throw new Error('Unauthorized');
            throw new Error('Failed to fetch user profile');
        }
        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
};

export const fetchRecommendations = async () => {
    try {
        const response = await fetch(`${API_BASE_URL}/api/recommendation`, {
            credentials: 'include'
        });
        if (!response.ok) {
            if (response.status === 401) throw new Error('Unauthorized');
            throw new Error('Failed to fetch recommendations');
        }
        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
};
