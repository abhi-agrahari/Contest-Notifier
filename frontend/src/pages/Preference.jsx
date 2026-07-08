import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  fetchUserPreferences, 
  saveUserPreference, 
  deleteUserPreference, 
  fetchNotificationSetting, 
  updateNotificationSetting,
  fetchUserProfile,
  updateUserHandles
} from '../api/api';
import Logo from '../components/Logo';
import './Preference.css';

const PLATFORMS = [
  'Codeforces', 
  'LeetCode', 
  'AtCoder', 
  'CodeChef', 
  'GeeksForGeeks', 
  'HackerEarth', 
  'HackerRank'
];

const Preference = () => {
  const [preferences, setPreferences] = useState({});
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [leetcodeHandle, setLeetcodeHandle] = useState('');
  const [codeforcesHandle, setCodeforcesHandle] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadPreferences();
  }, []);

  const loadPreferences = async () => {
    try {
      setLoading(true);
      const [data, setting, profile] = await Promise.all([
        fetchUserPreferences(),
        fetchNotificationSetting(),
        fetchUserProfile()
      ]);
      
      setNotificationsEnabled(setting);
      setLeetcodeHandle(profile.leetcodeHandle || '');
      setCodeforcesHandle(profile.codeforcesHandle || '');
      
      const prefMap = {};
      data.forEach(p => {
        prefMap[p.platform] = {
          enabled: p.enabled,
          notifyBeforeMinutes: p.notifyBeforeMinutes
        };
      });
      setPreferences(prefMap);
    } catch (err) {
      if (err.message === 'Unauthorized') {
        navigate('/login');
      } else {
        setError(err.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateHandles = async (e) => {
    e.preventDefault();
    try {
      setSaving(true);
      await updateUserHandles({
        leetcodeHandle,
        codeforcesHandle
      });
      // Optional: show success message
    } catch (err) {
      setError("Failed to update handles");
    } finally {
      setSaving(false);
    }
  };

  const handleGlobalToggle = async () => {
    try {
      setSaving(true);
      const newStatus = await updateNotificationSetting(!notificationsEnabled);
      setNotificationsEnabled(newStatus);
    } catch (err) {
      setError("Failed to update notification setting");
    } finally {
      setSaving(false);
    }
  };

  const handleToggle = async (platform) => {
    const isCurrentlyEnabled = preferences[platform]?.enabled || false;
    const currentMinutes = preferences[platform]?.notifyBeforeMinutes || 30;

    try {
      setSaving(true);
      if (isCurrentlyEnabled) {
        // Technically we could just disable it, but delete might be cleaner or as per API
        // For now let's just update it with enabled: false
        await saveUserPreference({
          platform,
          enabled: false,
          notifyBeforeMinutes: currentMinutes
        });
      } else {
        await saveUserPreference({
          platform,
          enabled: true,
          notifyBeforeMinutes: currentMinutes
        });
      }
      await loadPreferences();
    } catch (err) {
      setError("Failed to update preference");
    } finally {
      setSaving(false);
    }
  };

  const handleMinutesChange = async (platform, minutes) => {
    const isEnabled = preferences[platform]?.enabled || false;
    try {
      setSaving(true);
      await saveUserPreference({
        platform,
        enabled: isEnabled,
        notifyBeforeMinutes: parseInt(minutes)
      });
      await loadPreferences();
    } catch (err) {
      setError("Failed to update notification time");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="container">
      <nav className="nav">
        <Link to="/">
          <Logo size={40} />
        </Link>
        <div className="nav-actions">
          <Link to="/" className="back-link">Back to Home</Link>
        </div>
      </nav>

      <header className="hero">
        <h1>Your Preferences</h1>
        <p>Choose which platforms you want to get notified about and when.</p>
      </header>

      <main className="pref-main">
        {!loading && !error && (
            <div className="global-toggle-card">
                <div className="pref-info">
                    <h3>Email Notifications</h3>
                    <p>Master switch for all contest email alerts</p>
                </div>
                <button 
                    className={`toggle-btn ${notificationsEnabled ? 'on' : 'off'}`}
                    onClick={handleGlobalToggle}
                    disabled={saving}
                >
                    {notificationsEnabled ? 'Notifications On' : 'Notifications Off'}
                </button>
            </div>
        )}

        {!loading && !error && (
            <div className="global-toggle-card developer-handles-card">
                <div className="pref-info">
                    <h3>Developer Handles</h3>
                    <p>Set your handles for personalized recommendations</p>
                </div>
                <form onSubmit={handleUpdateHandles} className="handles-form">
                    <div className="handle-input-group">
                        <input 
                            type="text" 
                            placeholder="Codeforces Handle" 
                            value={codeforcesHandle}
                            onChange={(e) => setCodeforcesHandle(e.target.value)}
                            disabled={saving}
                        />
                        <input 
                            type="text" 
                            placeholder="LeetCode Username" 
                            value={leetcodeHandle}
                            onChange={(e) => setLeetcodeHandle(e.target.value)}
                            disabled={saving}
                        />
                    </div>
                    <button type="submit" className="save-handles-btn" disabled={saving}>
                        {saving ? 'Saving...' : 'Save Handles'}
                    </button>
                </form>
            </div>
        )}

        {loading ? (
          <div className="loader-box">
            <div className="pulse"></div>
          </div>
        ) : error ? (
          <div className="error-box">
            <p>{error}</p>
            <button onClick={loadPreferences}>Try again</button>
          </div>
        ) : (
          <div className="pref-list">
            {PLATFORMS.map(platform => {
              const pref = preferences[platform] || { enabled: false, notifyBeforeMinutes: 30 };
              return (
                <div key={platform} className={`pref-card ${pref.enabled ? 'active' : ''}`}>
                  <div className="pref-info">
                    <h3>{platform}</h3>
                    <p>{pref.enabled ? 'Notifications Active' : 'Notifications Disabled'}</p>
                  </div>
                  
                  <div className="pref-controls">
                    {pref.enabled && (
                      <div className="time-select">
                        <span>Notify me</span>
                        <select 
                          value={pref.notifyBeforeMinutes} 
                          onChange={(e) => handleMinutesChange(platform, e.target.value)}
                          disabled={saving}
                        >
                          <option value="5">5 mins</option>
                          <option value="15">15 mins</option>
                          <option value="30">30 mins</option>
                          <option value="60">1 hour</option>
                        </select>
                        <span>before</span>
                      </div>
                    )}
                    
                    <button 
                      className={`toggle-btn ${pref.enabled ? 'on' : 'off'}`}
                      onClick={() => handleToggle(platform)}
                      disabled={saving}
                    >
                      {pref.enabled ? 'Disable' : 'Enable'}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
};

export default Preference;
