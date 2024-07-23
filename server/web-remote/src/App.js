import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './AuthProvider';
import ProtectedRoute from './components/ProtectedRoute';
import LoginScreen from './components/login/LoginScreen';
import HomeScreen from "./components/HomeScreen";

function App() {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<LoginScreen />} />
                    <Route path="/home" element={<ProtectedRoute><HomeScreen /></ProtectedRoute>} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
