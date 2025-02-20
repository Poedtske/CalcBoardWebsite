import React from 'react'
import{
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

export default function RegistrationSuccess() {
  return (
    <main>
        <Container maxWidth="xs">
            <Box sx={{ mt: 8, color:'green' }}>
                <Typography variant="h3" align="center" gutterBottom>
                    Je hebt je successvol geregistreerd. Log je nu in
                </Typography>
                <Typography variant="h6" align="center" gutterBottom>
                    <a href="/login">Inloggen</a>
                </Typography>
            </Box>
        </Container>
    </main>
  )
}
