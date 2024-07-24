import React, {useState, useEffect} from 'react';
import {Button, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {LinearGradient} from "expo-linear-gradient";
import {router} from "expo-router";
import {useSession} from "../ctx";

const Login = () => {
    const [username, setUsername] = useState('');
    const [errorUsername, setErrorUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorPassword, setErrorPassword] = useState('');
    const {signIn} = useSession();

    const validate = () => {
        if (username === '') {
            setErrorUsername('Username is required');
            return false;
        }
        if (username !== 'admin') {
            setErrorUsername('Username is not correct');
            return false;
        }
        setErrorUsername('');
        if (password === '') {
            setErrorPassword('Password is required');
            return false;
        }
        if (password !== 'admin') {
            setErrorPassword('Password is not correct');
            return false;
        }
        setErrorPassword('');
        return true;
    };

    const login = () => {
        if (validate() && username === 'admin' && password === 'admin') {
            console.log('Login');
            signIn();
            router.replace("/(app)")
        }
    };

    return (
        <View style={{
            flex: 1,
            width: "100%",
            alignItems: "center"
        }}>
            <LinearGradient colors={["#4c669f", "#3b5998", "#192f6a"]} style={{
                width: "100%",
                justifyContent: "center",
                alignItems: "center",
                flex: 1
            }}>
                <View style={{
                    width: "40%",
                    alignItems: "center",
                    backgroundColor: "#fff",
                    paddingBottom: 40,
                    borderRadius: 16
                }}>
                    <Text style={{
                        fontSize: 24,
                        marginBottom: 24,
                        marginTop: 24
                    }}>Login</Text>
                    <TextInput value={username} onChangeText={setUsername} placeholder="Username" style={{
                        width: "90%",
                        height: 50,
                        borderColor: errorUsername ? 'red' : 'gray',
                        borderWidth: 1,
                        marginBottom: 24,
                        padding: 10,
                        borderRadius: 8
                    }}/>
                    {errorUsername ? <Text style={{color: 'red'}}>{errorUsername}</Text> : null}
                    <TextInput value={password} onChangeText={setPassword} placeholder="Password" style={{
                        width: "90%",
                        height: 50,
                        borderColor: errorPassword ? 'red' : 'gray',
                        borderWidth: 1,
                        marginBottom: 24,
                        padding: 10,
                        borderRadius: 8
                    }}/>
                    {errorPassword ? <Text style={{color: 'red'}}>{errorPassword}</Text> : null}
                    <TouchableOpacity
                        onPress={
                            login
                        }
                        style={{
                            width: "90%",
                            height: 40,
                            backgroundColor: "#4c669f",
                            alignItems: "center",
                            justifyContent: "center",
                            borderRadius: 8
                        }}>
                        <Text style={{
                            color: "#fff"
                        }}>Login</Text>
                    </TouchableOpacity>
                </View>
                <Text style={{
                    color: "#fff",
                    paddingVertical: 16,
                }}>
                    Made with ❤️ by <Text style={{fontWeight: "bold"}}>NQM</Text>
                </Text>
            </LinearGradient>

        </View>
    );
};


export default Login;
