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

export default function LoginSuccessful() {
  return (
    <main>
        <Container maxWidth="xs">
            <Box sx={{ mt: 8, color:'green' }}>
                <Typography variant="h3" align="center" gutterBottom>
                    Je bent nu ingelogd
                </Typography>
                <Typography variant="h6" align="center" gutterBottom>
                    <a href="/">Home</a>
                </Typography>
            </Box>
        </Container>
    </main>
  )
}
