import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Preference from './pages/Preference';
import Recommendation from './pages/Recommendation';

function App() {
  return (
    <Router>
      <div className="app-container">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/preferences" element={<Preference />} />
          <Route path="/recommendation" element={<Recommendation />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;

