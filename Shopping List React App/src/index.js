import React from 'react';
// Import the modern React DOM client API
import { createRoot } from 'react-dom/client';
// Import our main application component
import App from './App';

// Find the root DOM element in index.html
const container = document.getElementById('root');
// Create a root for the application
const root = createRoot(container);
console.log('here');
// Render the main App component inside the root
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
