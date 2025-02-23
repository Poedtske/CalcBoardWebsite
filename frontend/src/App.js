import React from 'react';
import { Route, Routes } from 'react-router-dom';

import NavBar from './components/NavBar';
import Home from './pages/home/Home';
import Footer from './components/Footer';
import './App.css';

function App() {
    return (
        <>
            <NavBar />
            <Routes>
                <Route path="/" element={<Home />} />
            </Routes>
            <Footer />
        </>
    );
}

export default App;