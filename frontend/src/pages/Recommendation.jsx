import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  fetchUserProfile,
  fetchContests,
  fetchRecommendations,
  fetchCodeforcesRating,
  fetchLeetCodeRating,
  logoutUser
} from '../api/api';
import Logo from '../components/Logo';
import ThemeToggle from '../components/ThemeToggle';
import './Recommendation.css';

function Recommendation() {
  const [loading, setLoading] = useState(true);
  const [profile, setProfile] = useState(null);
  const [cfRating, setCfRating] = useState(null);
  const [lcRating, setLcRating] = useState(null);
  const [contests, setContests] = useState([]);
  const [recommendations, setRecommendations] = useState(null);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  
  const navigate = useNavigate();

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      await fetchUserProfile();
      setIsLoggedIn(true);
      loadData();
    } catch {
      setIsLoggedIn(false);
      navigate('/login');
    }
  };

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [userProfile, allContests] = await Promise.all([
        fetchUserProfile(),
        fetchContests()
      ]);

      setProfile(userProfile);
      setContests(allContests);

      const hasCf = userProfile.codeforcesHandle && userProfile.codeforcesHandle.trim() !== '';
      const hasLc = userProfile.leetcodeHandle && userProfile.leetcodeHandle.trim() !== '';

      if (!hasCf || !hasLc) {
        setLoading(false);
        return;
      }

      const ratingsAndRecs = await Promise.allSettled([
        fetchCodeforcesRating(userProfile.codeforcesHandle),
        fetchLeetCodeRating(userProfile.leetcodeHandle),
        fetchRecommendations()
      ]);

      if (ratingsAndRecs[0].status === 'fulfilled') {
        setCfRating(ratingsAndRecs[0].value);
      }
      if (ratingsAndRecs[1].status === 'fulfilled') {
        setLcRating(ratingsAndRecs[1].value);
      }
      if (ratingsAndRecs[2].status === 'fulfilled') {
        const recData = ratingsAndRecs[2].value;
        setRecommendations(recData.recommended || []);
      } else {
        throw new Error('Failed to generate recommendations. Please try again.');
      }
    } catch (err) {
      if (err.message === 'Unauthorized') {
        navigate('/login');
      } else {
        setError(err.message || 'An error occurred while loading recommendations.');
      }
    } finally {
      setLoading(false);
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
      month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };

  const formatDuration = (durationMins) => {
    const hrs = Math.floor(durationMins / 60);
    const mins = durationMins % 60;
    return `${hrs}h ${mins}m`;
  };

  const getMatchedContest = (recommendedName) => {
    if (!recommendedName) return null;
    return contests.find(c => 
      c.name.toLowerCase().includes(recommendedName.toLowerCase()) || 
      recommendedName.toLowerCase().includes(c.name.toLowerCase())
    );
  };

  const hasHandlesConfigured = profile && 
    profile.codeforcesHandle && profile.codeforcesHandle.trim() !== '' &&
    profile.leetcodeHandle && profile.leetcodeHandle.trim() !== '';

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
        <h1>AI Contest Recommendations</h1>
        <p>Personalized contest recommendations based on your Codeforces & LeetCode statistics.</p>
      </header>

      <main className="rec-main">
        {loading ? (
          <div className="loading-state">
            <p>Loading AI recommendations...</p>
          </div>
        ) : error ? (
          <div className="error-state">
            <p>{error}</p>
            <button type="button" onClick={loadData} className="btn">Try Again</button>
          </div>
        ) : !hasHandlesConfigured ? (
          <div className="no-handles-card">
            <h2>Configure Your Coding Handles</h2>
            <p>Please set your Codeforces and LeetCode usernames in your Profile or Preferences to view custom recommendations.</p>
            <Link to="/profile" className="btn btn-primary">Set Up Handles in Profile</Link>
          </div>
        ) : (
          <div>
            <section className="stats-section">
              <h2>Your Profile Stats</h2>
              <div className="stats-grid">
                <div className="stat-card">
                  <h3>Codeforces (@{profile.codeforcesHandle})</h3>
                  {cfRating ? (
                    <div>
                      <p><strong>Current Rating:</strong> {cfRating.currentRating || 'N/A'}</p>
                      <p><strong>Max Rating:</strong> {cfRating.maxRating || 'N/A'}</p>
                    </div>
                  ) : (
                    <p className="text-muted">Loading live rating...</p>
                  )}
                </div>

                <div className="stat-card">
                  <h3>LeetCode (@{profile.leetcodeHandle})</h3>
                  {lcRating ? (
                    <div>
                      <p><strong>Current Rating:</strong> {lcRating.currentRating || 'N/A'}</p>
                      <p><strong>Max Rating:</strong> {lcRating.maxRating || 'N/A'}</p>
                    </div>
                  ) : (
                    <p className="text-muted">Loading live rating...</p>
                  )}
                </div>
              </div>
            </section>

            <section className="rec-section">
              <h2>Recommended Contests</h2>
              {recommendations && recommendations.length > 0 ? (
                <div className="rec-list">
                  {recommendations.map((rec, index) => {
                    const matched = getMatchedContest(rec.contest);
                    return (
                      <div key={index} className="rec-card">
                        <div className="rec-details">
                          <span className="rec-number">#{index + 1}</span>
                          <h3>{rec.contest}</h3>
                          <p className="rec-reason">💡 <strong>AI Mentor:</strong> {rec.reason}</p>
                        </div>

                        <div className="rec-action">
                          {matched ? (
                            <div>
                              <p className="text-muted">Start: {formatDate(matched.startTime)}</p>
                              <p className="text-muted">Duration: {formatDuration(matched.duration)}</p>
                              <a
                                href={matched.url}
                                target="_blank"
                                rel="noreferrer"
                                className="btn btn-primary"
                                style={{ marginTop: '0.5rem', display: 'inline-block' }}
                              >
                                View Contest
                              </a>
                            </div>
                          ) : (
                            <div>
                              <a 
                                href={`https://www.google.com/search?q=${encodeURIComponent(rec.contest)}`}
                                target="_blank"
                                rel="noreferrer"
                                className="btn"
                              >
                                Search Contest
                              </a>
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              ) : (
                <div className="empty-state">
                  <p>No recommendations available right now.</p>
                </div>
              )}
            </section>
          </div>
        )}
      </main>
    </div>
  );
}

export default Recommendation;
