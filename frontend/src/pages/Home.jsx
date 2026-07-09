import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { fetchContests } from '../api/api'
import Logo from '../components/Logo'
import './Home.css'

const Home = () => {
  const [contests, setContests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadContests();
  }, []);

  const loadContests = async () => {
    try {
      setLoading(true);
      const data = await fetchContests();
      setContests(data);
      setError(null);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (date) => {
    return new Date(date).toLocaleDateString(undefined, {
      month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };

  return (
    <div className="container">
      <nav className="nav">
        <Link to="/">
          <Logo size={40} />
        </Link>
        <div className="nav-actions">

          <Link to="/recommendation" className="pref-link">Recommendations</Link>
          <Link to="/preferences" className="pref-link">Preferences</Link>
          <Link to="/login" className="login-link">Login</Link>
        </div>
      </nav>

      <header className="hero">
        <h1>Stay Ahead of the Competition</h1>
        <p>Real-time contest schedule from all major platforms in one place.</p>
        <Link to="/recommendation" className="recommend-cta">
          Get AI Recommendation ✨
        </Link>
      </header>

      <main>

        {loading ? (
          <div className="loader-box">
            <div className="pulse"></div>
          </div>
        ) : error ? (
          <div className="error-box">
            <p>{error}</p>
            <button onClick={loadContests}>Try again</button>
          </div>
        ) : (
          <div className="grid">
            {contests.map((c, i) => (
              <div key={c.id || i} className="card">
                <span className="badge">{c.platform}</span>
                <h3>{c.name}</h3>
                <div className="meta">
                  <div className="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect><line x1="16" y1="2" x2="16" y2="6"></line><line x1="8" y1="2" x2="8" y2="6"></line><line x1="3" y1="10" x2="21" y2="10"></line></svg>
                    {formatDate(c.startTime)}
                  </div>
                  <div className="meta-item">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                    {Math.floor(c.duration / 60)}h {c.duration % 60}m
                  </div>
                </div>
                <a href={c.url} target="_blank" rel="noreferrer" className="action-btn">
                  Open Contest
                </a>
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default Home;
