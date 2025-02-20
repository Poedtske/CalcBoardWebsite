import React, { createContext, useContext, useState, useEffect } from "react";
import { getUserFromToken, getAuthToken, setAuthToken, removeAuthToken } from "../utils/jwtUtils";

const AuthContext = createContext();

// Hook to use Auth Context
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(null); // Initially null to track loading state
  const [userRole, setUserRole] = useState(null); // Initially null
  const [loading, setLoading] = useState(true); // Loading state to wait for token validation

  // Initialize Auth State
  useEffect(() => {
    const token = getAuthToken();
    if (token) {
      const user = getUserFromToken();
      if (user && user.exp * 1000 > Date.now()) { // Check token expiration
        setIsAuthenticated(true);
        setUserRole(user.role[0].authority); //get the userRole
      } else {
        removeAuthToken();
        setIsAuthenticated(false);
        setUserRole(null);
      }
    } else {
      setIsAuthenticated(false);
      setUserRole(null);
    }
    setLoading(false); // After validation, stop loading
  }, []);

  const login = () => {
    getAuthToken(); // Save token to localStorage
    const user = getUserFromToken();
    if (user) {
      setIsAuthenticated(true);
      setUserRole(user.role);
    }
  };

  const logout = () => {
    removeAuthToken();
    setIsAuthenticated(false);
    setUserRole(null);
  };

  if (loading) {
    // Optionally, you can show a loading spinner or message here if needed
    return <div>Loading...</div>;
  }

  const getRole=()=>{
    return userRole;
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, userRole, loading, login, logout, getRole }}>
      {children}
    </AuthContext.Provider>
  );
};
