import React, {useState, useEffect} from 'react';
import {View, Text, StyleSheet, FlatList, TouchableOpacity} from 'react-native';
import {router} from "expo-router";

const index = () => {
    const [clientsOnline, setClientsOnline] = useState([]);
    const [clientsOffline, setClientsOffline] = useState([]);

    useEffect(() => {
        // Fetch data from your backend or API
        // Replace this with your actual data fetching logic
        const fetchData = async () => {
            const onlineData = [
                // Example online data structure
                {
                    clientID: '123',
                    lastSeen: '2023-10-26T10:00:00Z',
                    dynamicData: {
                        clientIP: '192.168.1.1',
                        clientGeo: {country: 'US'},
                        device: {manufacture: 'Apple', model: 'iPhone 14'}
                    }
                },
                {
                    clientID: '456',
                    lastSeen: '2023-10-26T11:00:00Z',
                    dynamicData: {
                        clientIP: '10.0.0.1',
                        clientGeo: {country: 'GB'},
                        device: {manufacture: 'Samsung', model: 'Galaxy S23'}
                    }
                },
            ];
            const offlineData = [
                // Example offline data structure
                {
                    clientID: '789',
                    lastSeen: '2023-10-26T09:00:00Z',
                    dynamicData: {
                        clientIP: '172.16.0.1',
                        clientGeo: {country: 'DE'},
                        device: {manufacture: 'Google', model: 'Pixel 7'}
                    }
                },
                {
                    clientID: '012',
                    lastSeen: '2023-10-26T08:00:00Z',
                    dynamicData: {
                        clientIP: '192.168.0.1',
                        clientGeo: {country: 'FR'},
                        device: {manufacture: 'Xiaomi', model: 'Redmi Note 12'}
                    }
                },
            ];
            setClientsOnline(onlineData);
            setClientsOffline(offlineData);
        };
        fetchData();
    }, []);

    const renderItem = ({item}) => {
        const lsdate = new Date(item.lastSeen);
        const flagCss = item.dynamicData.clientGeo.country !== undefined ? ('flag ' + item.dynamicData.clientGeo.country).toLowerCase() : 'icon question mark';
        let deviceStr = '';
        deviceStr += item.dynamicData.device.manufacture !== undefined ? item.dynamicData.device.manufacture : '';
        deviceStr += item.dynamicData.device.model !== undefined ? ' (' + item.dynamicData.device.model + ')' : '';

        return (
            <View>
                <View style={styles.row}>
                    <Text style={styles.cell}>{item.clientID}</Text>
                    <Text style={styles.cell}><Text style={{fontSize: 18}}>{flagCss}</Text></Text>
                    <Text style={styles.cell}>{item.dynamicData.clientIP}</Text>
                    <Text style={styles.cell}>{deviceStr}</Text>
                    <Text style={styles.cell}>{lsdate.toLocaleString('en-GB', {timeZone: 'UTC'})}</Text>
                    <Text style={styles.cell}><TouchableOpacity
                        onPress={() => {
                            router.navigate("/(app)/manager", {clientID: item.clientID});
                        }}
                        style={{
                            backgroundColor: "#4CAF50",
                            border: "none",
                            color: "white",
                            padding: "15px 32px",
                            textAlign: "center",
                            textDecoration: "none",
                            display: "inline-block",
                            fontSize: "16px",
                            margin: "4px 2px",
                            cursor: "pointer",
                            borderRadius: "16px",
                        }
                        }><Text>Manage</Text></TouchableOpacity></Text>
                </View>
            </View>
        );
    };

    return (
        <View style={styles.container}>
            <View style={{
                width: "90%",
                borderRadius: 16
            }}>
                <Text style={styles.header}>Online</Text>
                <FlatList
                    data={clientsOnline}
                    renderItem={renderItem}
                    keyExtractor={(item) => item.clientID}
                    style={styles.table}
                    ListHeaderComponent={
                        <View style={styles.tableHeader}>
                            <Text style={styles.tableHeaderCell}>Name</Text>
                            <Text style={styles.tableHeaderCell}>Country</Text>
                            <Text style={styles.tableHeaderCell}>IP</Text>
                            <Text style={styles.tableHeaderCell}>Device</Text>
                            <Text style={styles.tableHeaderCell}>Last Seen</Text>
                            <Text style={styles.tableHeaderCell}>Manager</Text>
                        </View>
                    }
                />

                <Text style={styles.header}>Offline</Text>
                <FlatList
                    data={clientsOffline}
                    renderItem={renderItem}
                    keyExtractor={(item) => item.clientID}
                    style={styles.table}
                    ListHeaderComponent={
                        <View style={styles.tableHeader}>
                            <Text style={styles.tableHeaderCell}>Name</Text>
                            <Text style={styles.tableHeaderCell}>Country</Text>
                            <Text style={styles.tableHeaderCell}>IP</Text>
                            <Text style={styles.tableHeaderCell}>Device</Text>
                            <Text style={styles.tableHeaderCell}>Last Seen</Text>
                            <Text style={styles.tableHeaderCell}>Manager</Text>
                        </View>
                    }
                />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 16,
        alignItems: 'center',
        backgroundColor: '#fff',
    },
    header: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 16,
    },
    table: {
        marginBottom: 32,
    },
    tableHeader: {
        flexDirection: 'row',
        backgroundColor: '#f0f0f0',
        padding: 8,
    },
    tableHeaderCell: {
        flex: 1,
        fontWeight: 'bold',
    },
    row: {
        flexDirection: 'row',
        padding: 8,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
    },
    cell: {
        flex: 1,
        textAlign: 'center',
        borderColor: '#ddd',
        borderWidth: 1,
        padding: 8,
        backgroundColor: '#f9f9f9',
    },
});

export default index;
