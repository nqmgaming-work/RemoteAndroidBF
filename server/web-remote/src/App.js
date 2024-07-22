import React from 'react';
import DeviceRegister from './components/DeviceRegister';
import NotificationSender from './components/NotificationSender';

function App() {
  return (
      <div className="App">
        <h1>Device Management</h1>
        <DeviceRegister />
        <NotificationSender />
      </div>
  );
}

export default App;
