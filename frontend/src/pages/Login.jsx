import { Link } from 'react-router-dom';
import Logo from '../components/Logo';
import './Login.css';

function Login() {
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

  const handleGoogleLogin = () => {
    window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <Logo />
          <h1>Sign In</h1>
          <p>Sign in to access recommendations and preferences</p>
        </div>

        <button type="button" onClick={handleGoogleLogin} className="google-login-btn">
          Continue with Google
        </button>

        <div className="login-footer">
          <Link to="/" className="btn">← Back to Home</Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
