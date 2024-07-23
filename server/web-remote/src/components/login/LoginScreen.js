import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {useAuth} from "../../AuthProvider";

function LoginScreen() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();


    const handleLogin = (e) => {
        e.preventDefault();
        if (username === 'admin' && password === 'admin') {
            login();
            navigate('/home');
        } else {
            alert('Invalid credentials');
        }
    };

    return (
        <div style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            height: '100vh',
            backgroundImage: 'url("https://source.unsplash.com/random/1600x900")',
            backgroundSize: 'cover',
            backgroundPosition: 'center',
            color: 'white',
        }}>
            <div style={{
                backgroundColor: 'rgba(0, 0, 0, 0.5)',
                padding: '20px',
                borderRadius: '10px',
                boxShadow: '0 0 20px rgba(0, 0, 0, 0.5)',
            }}>
                <h2 style={{
                    marginBottom: '20px',
                    fontFamily: 'Arial, sans-serif',
                    textAlign: 'center',
                }}>Login</h2>
                <form onSubmit={handleLogin} style={{
                    display: 'flex',
                    flexDirection: 'column',
                    gap: '10px',
                    width: '300px',
                }}>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '5px',
                    }}>
                        <label style={{
                            marginBottom: '5px',
                            fontSize: '18px',
                            fontWeight: 'bold',
                        }}>Username:</label>
                        <input
                            type="text"
                            value={username}
                            style={{
                                padding: '10px',
                                fontSize: '16px',
                                borderWidth: '1px',
                                borderRadius: '5px',
                            }}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </div>
                    <div style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '5px',
                    }}>
                        <label style={{
                            marginBottom: '5px',
                            fontSize: '18px',
                            fontWeight: 'bold',
                        }}>Password:</label>
                        <input
                            type="password"
                            value={password}
                            style={{
                                padding: '10px',
                                fontSize: '16px',
                                borderWidth: '1px',
                                borderRadius: '5px',
                            }}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>
                    <button type="submit" style={{
                        padding: '10px',
                        backgroundColor: 'blue',
                        color: 'white',
                        border: 'none',
                        cursor: 'pointer',
                        width: '100%',
                        borderRadius: '5px',
                        fontSize: '16px',
                        fontWeight: 'bold',
                        transition: 'background-color 0.3s',
                    }}
                            onMouseOver={(e) => e.currentTarget.style.backgroundColor = 'darkblue'}
                            onMouseOut={(e) => e.currentTarget.style.backgroundColor = 'blue'}>
                        Login
                    </button>
                </form>
            </div>
        </div>
    );
}

export default LoginScreen;
