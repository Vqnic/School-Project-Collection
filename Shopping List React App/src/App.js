import React, { useState, useEffect } from 'react';
import './style.css'; // Assuming StackBlitz has a style.css file for basic styling
import { db, shoppingList, auth, googleProvider, apiEndpoints } from './firebase';
import { addDoc, getDocs, doc, setDoc, onSnapshot, serverTimestamp, deleteDoc } from 'firebase/firestore';
import { signInWithPopup } from 'firebase/auth';

function Title() {
  return <h1>Task List</h1>;
}

function ClearAll() {
  return <h1>Clear All</h1>;
}


// Define the main App component using a function (Functional Component)
function App() {

  const [user, setUser] = useState(null);

  const [taskList, setTaskList] = useState([]);
  //const [count, setCount] = useState(5);
  const [selectedValue, setSelectedValue] = useState('importance');
  // add an action to constantly listen for db changes
  useEffect(() => {
    const unsubscribe = onSnapshot(shoppingList, (snapshot) => {
      const itemsList = snapshot.docs.map((doc) => ({
        id: doc.data().id,
        content: doc.data().content,
        style: doc.data().style,
        createdAt: doc.data().createdAt,
        createdBy: doc.data().createdBy
      }));
      setTaskList(itemsList);
    });
    return () => unsubscribe();
  }, []);
  // Listen for authentication state changes
  useEffect(() => {
  const unsubscribe = auth.onAuthStateChanged((currentUser) => {
    setUser(currentUser);
  });
  return () => unsubscribe();
}, []);


  const changeStyle = async (event) => {
    if(user === null) {
      alert("Please sign in below to edit tasks.");
      return;
    }
    const id = event.target.id;
    const currentStyle = event.target.className;

    try {
      const response = await fetch(apiEndpoints.changeStyle, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id
        })
      });

      const result = await response.json();
      console.log("Cloud function response: ", result);
    } catch (error) {
      console.error("Error calling cloud function: ", error);
    }
  };

  const saveData = async () => {
    const dataSnapshot = await getDocs(shoppingList);
    dataSnapshot.forEach((doc) => {
      console.log(doc.id, ' => ', doc.data());
    })
  };

  // Sort the task list based on the selected value
  const getSortedTaskList = () => {
    const sortedList = [...taskList];
    
    if (selectedValue === 'importance') {
      // Sort by importance: hot -> cool -> complete
      const priorityOrder = { 'hot': 1, 'cool': 2, 'complete': 3 };
      sortedList.sort((a, b) => {
        return (priorityOrder[a.style] || 999) - (priorityOrder[b.style] || 999);
      });
    } else if (selectedValue === 'time-created') {
      // Sort by creation time (earliest first)
      sortedList.sort((a, b) => {
        if (!a.createdAt) return 1;
        if (!b.createdAt) return -1;
        return a.createdAt.seconds - b.createdAt.seconds;
      });
    } else if (selectedValue === 'user') {
      // Sort by user alphabetically
      sortedList.sort((a, b) => {
        const userA = a.createdBy || '';
        const userB = b.createdBy || '';
        return userA.localeCompare(userB);
      });
    }
    
    return sortedList;
  };

  const sortedTaskList = getSortedTaskList();

  const listItems = sortedTaskList.map((task) => (
    <li key={task.id} id={task.id} className={task.style} onClick={changeStyle}>
      {task.content}
    </li>
  ));

  async function deleteItems() {
    if(user === null) {
      alert("Please sign in below to edit tasks.");
      return;
    }

    try {
      const response = await fetch(apiEndpoints.clearList, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        }
      });
      const result = await response.json();
      console.log("Cloud function response: ", result);
    } catch (error) {
      console.error("Error calling cloud function: ", error);
    }
  }


  // Sanitize input to prevent XSS and other attacks
  function sanitizeInput(input) {
    // Remove leading/trailing whitespace
    let sanitized = input.trim();
    
    // Remove any HTML tags
    sanitized = sanitized.replace(/<[^>]*>/g, '');
    
    // Remove script tags and javascript: protocol
    sanitized = sanitized.replace(/javascript:/gi, '');
    sanitized = sanitized.replace(/on\w+=/gi, '');
    
    // Limit to reasonable length
    const MAX_LENGTH = 30;
    if (sanitized.length > MAX_LENGTH) {
      sanitized = sanitized.substring(0, MAX_LENGTH);
    }
    
    return sanitized;
  }

  async function addTask(event) {
    if(user === null) {
      alert("Please sign in below to edit tasks.");
      return;
    }
    if (event.key === 'Enter') {
      event.preventDefault();
      
      // Sanitize and validate the input
      const rawInput = event.target.value;
      const sanitizedContent = sanitizeInput(rawInput);
      
      // Prevent empty submissions
      if (!sanitizedContent || sanitizedContent.length === 0) {
        event.target.value = "";
        return;
      }

      try {
        const response = await fetch(apiEndpoints.addItem, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            content: sanitizedContent,
            style: 'cool',
            createdBy: user.displayName
          })
        });
        const result = await response.json();
        console.log("Cloud function response: ", result);
      } catch (error) {
        console.error("Error calling cloud function: ", error);
      }
    }
  }

  async function sortBy(event) {
    setSelectedValue(event.target.value);
  }

  async function handleSignIn() {
    if (user) {
      // User is signed in, so sign them out
      await auth.signOut();
      setUser(null);
    } else {
      // User is not signed in, so sign them in with Google
      try {
        const result = await signInWithPopup(auth, googleProvider);
        setUser(result.user);
      } catch (error) {
        console.error('Error signing in:', error);
      }
    }
  }

  return (
    <div>
      <Title />
      <label htmlFor="sort-by">Sort By: </label>
      <select id="sort-by" value={selectedValue} onChange={sortBy}>
        <option value="importance">Importance</option>
        <option value="time-created">Time Created</option>
        <option value="user">Created By</option>
      </select>
      <ul>{listItems}</ul>
      <form onKeyDown={addTask}>
        <label>
          <input type="text" maxlength="30" placeholder="Enter your item" />
        </label>
      </form>
      <button onClick={deleteItems}>
        Clear Completed Tasks
      </button>
      <button onClick={handleSignIn}>
        {user ? `Sign out from ${user.displayName}` : 'Sign in with Google'}
      </button>
    </div>
  );
}

export default App;