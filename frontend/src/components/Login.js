import { React, useState } from 'react';
import {
  Card,
  CardContent,
  Typography,
  CardActions,
  Button,
  Divider,
  Grid,
  TextField,
  Container,
  Box
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { request } from '../AxiosConfig';
import { useAuth } from '../context/AuthContext';
import { getAuthToken, setAuthToken } from "../utils/jwtUtils"; // Import JWT utility functions

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleLogin = async (e) => {
    e.preventDefault();

    setError('');

    const loginData = {
      email,
      password
    };

    // Send login request to the server
    try {
      const response = await request('POST', '/login', loginData);
      if (response.status === 200) {
        setAuthToken(response.data.token);
        login();
        navigate('/loginSuccessful');
      } else {
        const errorData = await response.json();
        setError(errorData.message || 'Login failed for user. Please retry!');
      }
    } catch (err) {
      setError('An error occurred. Please retry');
    }
  };

  return (
    <main>
      <Container
        maxWidth="xs"
        sx={{
          backgroundColor: "black",
          color: "white",
          borderRadius: 2,
          padding: 3,
          boxShadow: 3,
        }}
      >
        <Box
          component="form"
          onSubmit={handleLogin}
          sx={{
            mt: 4,
            display: "flex",
            flexDirection: "column",
            gap: 2,
          }}
        >
          <Typography
            variant="h5"
            component="h1"
            align="center"
            sx={{ color: "white" }}
          >
            Login
          </Typography>

          {error && (
            <Typography
              variant="body2"
              color="error"
              align="center"
              sx={{ color: "red" }}
            >
              {error}
            </Typography>
          )}

          <TextField
            label="Email"
            variant="outlined"
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            InputLabelProps={{
              style: { color: "white" },
            }}
            InputProps={{
              style: { color: "white", backgroundColor: "#333" },
            }}
          />

          <TextField
            label="Passwoord"
            variant="outlined"
            type="password"
            fullWidth
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            InputLabelProps={{
              style: { color: "white" },
            }}
            InputProps={{
              style: { color: "white", backgroundColor: "#333" },
            }}
          />

          <Button
            type="submit"
            variant="contained"
            fullWidth
            sx={{
              backgroundColor: "white",
              color: "black",
              "&:hover": {
                backgroundColor: "gray",
                color: "white",
              },
            }}
          >
            Login
          </Button>

          {/* Register Button */}
          <Button
            variant="outlined"
            fullWidth
            sx={{
              marginTop: 2,
              color: "white",
              borderColor: "white",
              "&:hover": {
                backgroundColor: "gray",
                color: "white",
              },
            }}
            onClick={() => navigate('/register')} // Navigate to the register page
          >
            Registreer hier
          </Button>
        </Box>
      </Container>
    </main>
  );
}
