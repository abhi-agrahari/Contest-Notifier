import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { 
  fetchUserPreferences, 
  saveUserPreference, 
  fetchNotificationSetting, 
  updateNotificationSetting,
  fetchUserProfile,
  logoutUser
} from '../api/api';
import Logo from '../components/Logo';
import ThemeToggle from '../components/ThemeToggle';
import './Preference.css';

const PLATFORMS = [
  'Codeforces', 'LeetCode', 'AtCoder', 'CodeChef', 
  'GeeksForGeeks', 'HackerEarth', 'HackerRank'
];

function Preference() {
  const [preferences, setPreferences] = useState({});
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      await fetchUserProfile();
      setIsLoggedIn(true);

      const [prefData, globalSetting] = await Promise.all([
        fetchUserPreferences(),
        fetchNotificationSetting()
      ]);
      
      setNotificationsEnabled(globalSetting);

      const prefMap = {};
      prefData.forEach(p => {
        prefMap[p.platform] = {
          enabled: p.enabled,
          notifyBeforeMinutes: p.notifyBeforeMinutes
        };
      });
      setPreferences(prefMap);
      setError(null);
    } catch (err) {
      if (err.message === 'Unauthorized') {
        navigate('/login');
      } else {
        setError(err.message || 'Failed to load preferences');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try { await logoutUser(); } finally { navigate('/login'); }
  };

  const handleGlobalToggle = async () => {
    try {
      setSaving(true);
      const newStatus = await updateNotificationSetting(!notificationsEnabled);
      setNotificationsEnabled(newStatus);
    } catch {
      setError('Failed to update notification setting');
    } finally {
      setSaving(false);
    }
  };

  const updatePreference = async (platform, enabled, notifyBeforeMinutes) => {
    try {
      setSaving(true);
      await saveUserPreference({ platform, enabled, notifyBeforeMinutes });
      await loadData();
    } catch {
      setError('Failed to update preference');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="container">
      <nav className="nav">
        <Link to="/" className="nav-brand"><Logo /></Link>
        <button type="button" className="mobile-nav-toggle" onClick={() => setMenuOpen(!menuOpen)}>☰</button>
        <div className={`nav-actions ${menuOpen ? 'open' : ''}`}>
          <ThemeToggle />
          <Link to="/" className="btn">Back to Home</Link>
          <Link to="/profile" className="btn">Profile</Link>
          {isLoggedIn ? (
            <button type="button" onClick={handleLogout} className="btn btn-danger">Logout</button>
          ) : (
            <Link to="/login" className="btn btn-primary">Login</Link>
          )}
        </div>
      </nav>

      <header className="hero">
        <h1>Preferences</h1>
        <p>Manage contest email notifications and reminder times.</p>
      </header>

      <main className="pref-main">
        {loading ? (
          <div className="loading-state"><p>Loading preferences...</p></div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
            <button type="button" onClick={loadData} className="btn">Try Again</button>
          </div>
        ) : (
          <div>
            <div className="pref-card global-card">
              <div>
                <h3>Email Notifications</h3>
                <p className="text-muted">Master switch for all contest email alerts</p>
              </div>
              <button 
                type="button"
                className={`btn ${notificationsEnabled ? 'btn-danger' : 'btn-primary'}`}
                onClick={handleGlobalToggle}
                disabled={saving}
              >
                {notificationsEnabled ? 'Disable All' : 'Enable All'}
              </button>
            </div>

            <h2 className="section-title">Platform Preferences</h2>
            <div className="pref-list">
              {PLATFORMS.map(platform => {
                const pref = preferences[platform] || { enabled: false, notifyBeforeMinutes: 30 };
                return (
                  <div key={platform} className="pref-card">
                    <div>
                      <h3>{platform}</h3>
                      <p className="text-muted">
                        {pref.enabled ? 'Notifications Enabled' : 'Notifications Disabled'}
                      </p>
                    </div>

                    <div className="pref-controls">
                      {pref.enabled && (
                        <div className="time-select">
                          <label htmlFor={`select-${platform}`}>Notify before:</label>
                          <select 
                            id={`select-${platform}`}
                            value={pref.notifyBeforeMinutes} 
                            onChange={(e) => updatePreference(platform, true, parseInt(e.target.value, 10))}
                            disabled={saving}
                          >
                            <option value="5">5 mins</option>
                            <option value="15">15 mins</option>
                            <option value="30">30 mins</option>
                            <option value="60">1 hour</option>
                          </select>
                        </div>
                      )}

                      <button 
                        type="button"
                        className={`btn ${pref.enabled ? 'btn-danger' : 'btn-primary'}`}
                        onClick={() => updatePreference(platform, !pref.enabled, pref.notifyBeforeMinutes)}
                        disabled={saving}
                      >
                        {pref.enabled ? 'Disable' : 'Enable'}
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default Preference;
