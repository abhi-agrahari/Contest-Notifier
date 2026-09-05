import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { fetchContests, fetchUserProfile, refreshContests, logoutUser } from '../api/api';
import Logo from '../components/Logo';
import ThemeToggle from '../components/ThemeToggle';
import './Home.css';

function Home() {
  const [contests, setContests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadContests();
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      await fetchUserProfile();
      setIsLoggedIn(true);
    } catch {
      setIsLoggedIn(false);
    }
  };

  const loadContests = async () => {
    try {
      setLoading(true);
      const data = await fetchContests();
      setContests(data);
      setError(null);
    } catch (err) {
      setError(err.message || 'Failed to load contests');
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    try {
      setRefreshing(true);
      await refreshContests();
      await loadContests();
    } catch (err) {
      setError(err.message || 'Failed to refresh contests');
    } finally {
      setRefreshing(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logoutUser();
    } finally {
      setIsLoggedIn(false);
      navigate('/login');
    }
  };

  const formatDate = (dateStr) => {
    return new Date(dateStr).toLocaleString(undefined, {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatDuration = (minutes) => {
    const hrs = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hrs}h ${mins}m`;
  };

  return (
    <div className="container">
      <nav className="nav">
        <Link to="/" className="nav-brand">
          <Logo />
        </Link>
        
        <button
          type="button"
          className="mobile-nav-toggle"
          onClick={() => setMenuOpen(!menuOpen)}
        >
          ☰
        </button>

        <div className={`nav-actions ${menuOpen ? 'open' : ''}`}>
          <ThemeToggle />
          <button
            type="button"
            onClick={handleRefresh}
            className="btn"
            disabled={refreshing}
          >
            {refreshing ? 'Refreshing...' : '🔄 Refresh'}
          </button>
          <Link to="/preferences" className="btn">Preferences</Link>
          <Link to="/profile" className="btn">Profile</Link>
          {isLoggedIn ? (
            <button type="button" onClick={handleLogout} className="btn btn-danger">
              Logout
            </button>
          ) : (
            <Link to="/login" className="btn btn-primary">
              Login
            </Link>
          )}
        </div>
      </nav>

      <header className="hero">
        <h1>Contest Schedule</h1>
        <p>Upcoming coding contests from all major competitive programming platforms.</p>
      </header>

      <main>
        {/* Recommendation Feature Card */}
        <div className="recommendation-banner">
          <div>
            <h2>✨ AI Contest Recommendations</h2>
            <p className="text-muted">Get personalized contest suggestions based on your Codeforces and LeetCode ratings.</p>
          </div>
          <Link to="/recommendation" className="btn btn-primary">
            Get Recommendations →
          </Link>
        </div>

        {loading ? (
          <div className="loading-state">
            <p>Loading contests...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
            <button type="button" onClick={loadContests} className="btn">
              Try Again
            </button>
          </div>
        ) : contests.length === 0 ? (
          <div className="empty-state">
            <p>No upcoming contests found.</p>
          </div>
        ) : (
          <div className="contest-grid">
            {contests.map((contest, index) => (
              <div key={contest.id || index} className="contest-card">
                <span className="platform-badge">{contest.platform}</span>
                <h2>{contest.name}</h2>
                <p className="contest-meta">📅 Start: {formatDate(contest.startTime)}</p>
                <p className="contest-meta">⏳ Duration: {formatDuration(contest.duration)}</p>
                <a
                  href={contest.url}
                  target="_blank"
                  rel="noreferrer"
                  className="btn btn-primary"
                >
                  Open Contest
                </a>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}

export default Home;
