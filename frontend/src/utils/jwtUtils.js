import { jwtDecode } from "jwt-decode"; // Ensure proper import syntax

//for managing authtoken

// Get the authentication token from sessionStorage
export const getAuthToken = () => {
  if (sessionStorage.getItem("auth_token") != null) {
    return sessionStorage.getItem("auth_token");
  } else {
    return false;
  }
};

// Remove the authentication token from sessionStorage
export const removeAuthToken = () => {
  sessionStorage.removeItem("auth_token");
};

// Set the authentication token in sessionStorage
export const setAuthToken = (token) => {
  sessionStorage.setItem("auth_token", token);
};

// Decode JWT and get user details
export const getUserFromToken = () => {
  const token = getAuthToken(); // Use getAuthToken to retrieve the token
  if (token) {
    try {
      const decoded = jwtDecode(token);
      return decoded; // Returns the decoded JWT payload (e.g., { role: 'USER', ... })
    } catch (error) {
      console.error("Invalid token", error);
      return "InvalidToken";
    }
  }
  return null;
};

export const getUserRole = () => {
  const user = getUserFromToken();
  return user?.role[0]?.authority || null;
};

// Check if the user has a specific role
export const hasRole = (role) => {
  const user = getUserFromToken();
  return user && user.role === role;
};

// Check if the user is authenticated
export const isAuthenticated = () => {
  return getUserFromToken() !== null;
};
