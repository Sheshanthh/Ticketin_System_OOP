import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AdminDashboard } from './components/AdminDashboard';
import CustomerDashboard from './components/CustomerDashboard';  // Import CustomerDashboard

function App() {
  return (
    <Router>
      <Routes>
        {/* Define the route for AdminDashboard */}
        <Route path="/admin-dashboard" element={<AdminDashboard />} />

        {/* Define the route for CustomerDashboard */}
        <Route path="/" element={<CustomerDashboard />} />

        {/* Add other routes here as needed */}
      </Routes>
    </Router>
  );
}

export default App;
