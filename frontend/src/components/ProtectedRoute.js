import { useAuth } from '../context/AuthContext';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRole }) => {
  const { isAuthenticated, userRole, loading } = useAuth(); // Destructure loading state from context

  // Wait for the loading state to finish (authentication check)
  if (loading) {
    // Optionally, show a loading spinner or nothing while waiting
    return <div>Loading...</div>;
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    console.log(isAuthenticated);
    return <Navigate to="/login" />;
  }

  // Redirect to home if the role is not authorized
  if (requiredRole && userRole !== requiredRole) {
    console.log("role: " + userRole);
    console.log("requiredRole: " + requiredRole);
    return <Navigate to="/" />;
  }

  // If authenticated and role matches, render children
  return children;
};

export default ProtectedRoute;
