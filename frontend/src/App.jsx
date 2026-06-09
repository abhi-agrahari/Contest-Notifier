import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';

function App() {
  return (
    <Router>
      <div className="app-container">
        {/* Navigation can be added here later */}
        <Routes>
          <Route path="/" element={<Home />} />
          {/* Add more routes here as you build new pages */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
