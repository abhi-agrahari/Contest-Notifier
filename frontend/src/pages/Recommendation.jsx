import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  fetchUserProfile,
  fetchContests,
  fetchRecommendations,
  fetchCodeforcesRating,
  fetchLeetCodeRating
} from '../api/api';
import Logo from '../components/Logo';
import './Recommendation.css';

const Recommendation = () => {
  const [loading, setLoading] = useState(true);
  const [loadingStep, setLoadingStep] = useState(0);
  const [profile, setProfile] = useState(null);
  const [cfRating, setCfRating] = useState(null);
  const [lcRating, setLcRating] = useState(null);
  const [contests, setContests] = useState([]);
  const [recommendations, setRecommendations] = useState(null);
  const [error, setError] = useState(null);
  
  const navigate = useNavigate();

  const loadingMessages = [
    'Connecting to database...',
    'Fetching your coding profiles...',
    'Retrieving ratings from Codeforces & LeetCode...',
    'Loading upcoming contest schedule...',
    'Generating AI Recommendations via Gemini...',
    'Polishing your personalized dashboard...'
  ];

  // Cycle loading messages for a premium feel
  useEffect(() => {
    if (!loading) return;
    const interval = setInterval(() => {
      setLoadingStep((prev) => (prev < loadingMessages.length - 1 ? prev + 1 : prev));
    }, 1500);
    return () => clearInterval(interval);
  }, [loading]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);

      // 1. Fetch profile and upcoming contests
      const [userProfile, allContests] = await Promise.all([
        fetchUserProfile(),
        fetchContests()
      ]);

      setProfile(userProfile);
      setContests(allContests);

      // Checking if handles are missing
      const hasCf = userProfile.codeforcesHandle && userProfile.codeforcesHandle.trim() !== '';
      const hasLc = userProfile.leetcodeHandle && userProfile.leetcodeHandle.trim() !== '';

      if (!hasCf || !hasLc) {
        setLoading(false);
        return; // Page will display a "please set handles" prompt
      }

      // 2. Fetch recommendations and user ratings in parallel
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

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString(undefined, {
      weekday: 'short', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };

  const getFormatDuration = (durationMins) => {
    const hrs = Math.floor(durationMins / 60);
    const mins = durationMins % 60;
    return `${hrs}h ${mins}m`;
  };

  // Match AI recommended contest to actual contest data from API
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
        <Link to="/">
          <Logo size={40} />
        </Link>
        <div className="nav-actions">
          <Link to="/" className="back-link">Back to Home</Link>
        </div>
      </nav>

      <header className="hero">
        <h1 className="title-gradient">AI Contest Recommendations</h1>
        <p className="subtitle">Custom competitive programming recommendations curated by Gemini based on your live statistics.</p>
      </header>

      <main className="rec-main">
        {loading ? (
          <div className="rec-loader-container">
            <div className="pulse-loader"></div>
            <p className="loading-text">{loadingMessages[loadingStep]}</p>
          </div>
        ) : error ? (
          <div className="rec-error-card">
            <div className="error-icon">⚠️</div>
            <h3>Unable to Curate Recommendations</h3>
            <p>{error}</p>
            <button onClick={loadData} className="retry-btn">Try Again</button>
          </div>
        ) : !hasHandlesConfigured ? (
          <div className="no-handles-card">
            <div className="handle-badge-icons">
              <span className="platform-icon cf">CF</span>
              <span className="platform-icon lc">LC</span>
            </div>
            <h2>Configure Your Coding Handles</h2>
            <p>
              To offer personalized contest recommendations, our AI model needs to evaluate your rating history and recent performances on Codeforces and LeetCode.
            </p>
            <div className="cta-group">
              <Link to="/preferences" className="setup-handles-btn">Set Up Handles in Preferences</Link>
            </div>
          </div>
        ) : (
          <>
            {/* User Statistics Dashboard */}
            <section className="stats-dashboard">
              <h2 className="section-title">Your Developer Profile</h2>
              <div className="stats-grid">
                {/* Codeforces Stats */}
                <div className="stat-card codeforces-card">
                  <div className="stat-header">
                    <span className="platform-tag cf-tag">Codeforces</span>
                    <span className="handle-txt">@{profile.codeforcesHandle}</span>
                  </div>
                  {cfRating ? (
                    <div className="rating-display">
                      <span className="label">Current Rating</span>
                      <span className="value cf-color">{cfRating.currentRating || 'N/A'}</span>
                      <span className="label max-label">Max Rating: {cfRating.maxRating || 'N/A'}</span>
                    </div>
                  ) : (
                    <div className="rating-display-loading">Loading live profile...</div>
                  )}
                </div>

                {/* LeetCode Stats */}
                <div className="stat-card leetcode-card">
                  <div className="stat-header">
                    <span className="platform-tag lc-tag">LeetCode</span>
                    <span className="handle-txt">@{profile.leetcodeHandle}</span>
                  </div>
                  {lcRating ? (
                    <div className="rating-display">
                      <span className="label">Current Rating</span>
                      <span className="value lc-color">{lcRating.currentRating || 'N/A'}</span>
                      <span className="label max-label">Max Rating: {lcRating.maxRating || 'N/A'}</span>
                    </div>
                  ) : (
                    <div className="rating-display-loading">Loading live profile...</div>
                  )}
                </div>
              </div>
            </section>

            {/* AI Curations */}
            <section className="curated-section">
              <h2 className="section-title">AI Personalized Recommendations</h2>
              {recommendations && recommendations.length > 0 ? (
                <div className="curated-list">
                  {recommendations.map((rec, index) => {
                    const matched = getMatchedContest(rec.contest);
                    return (
                      <div key={index} className="recommendation-item">
                        <div className="rec-info-side">
                          <div className="rec-badge-row">
                            <span className="curated-badge">Recommendation #{index + 1}</span>
                            {matched && <span className="platform-badge">{matched.platform}</span>}
                          </div>
                          <h3>{rec.contest}</h3>
                          
                          <div className="ai-reason-bubble">
                            <span className="ai-sparkle">✨</span>
                            <div className="ai-text">
                              <strong>AI Mentor:</strong> {rec.reason}
                            </div>
                          </div>
                        </div>

                        <div className="rec-meta-side">
                          {matched ? (
                            <>
                              <div className="meta-row">
                                <span className="meta-label">Starts</span>
                                <span className="meta-value">{formatDate(matched.startTime)}</span>
                              </div>
                              <div className="meta-row">
                                <span className="meta-label">Duration</span>
                                <span className="meta-value">{getFormatDuration(matched.duration)}</span>
                              </div>
                              <a
                                href={matched.url}
                                target="_blank"
                                rel="noreferrer"
                                className="register-btn"
                              >
                                View / Register
                              </a>
                            </>
                          ) : (
                            <div className="no-match-meta">
                              <p>Contest details loaded by AI recommendations engine.</p>
                              <a 
                                href={`https://www.google.com/search?q=${encodeURIComponent(rec.contest)}`}
                                target="_blank"
                                rel="noreferrer"
                                className="search-btn"
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
                <div className="no-recommendations-box">
                  <p>Our AI could not find any suitable upcoming contests for your skill profile at this moment. Stay tuned!</p>
                </div>
              )}
            </section>
          </>
        )}
      </main>
    </div>
  );
};

export default Recommendation;
