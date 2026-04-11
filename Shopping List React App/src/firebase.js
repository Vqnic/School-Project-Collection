import { initializeApp } from 'firebase/app';
import { getFirestore, collection } from 'firebase/firestore';
import { getAuth, onAuthStateChanged, signInWithRedirect, GoogleAuthProvider} from 'firebase/auth';

const firebaseConfig = { /*not uploaded for obvious security reasons*/ };

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const shoppingList = collection(db, 'mylist');
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider()


// API endpoints from firebase.json rewrites
export const apiEndpoints = {
  addItem: '/api/addItem',
  clearList: '/api/clearList',
  changeStyle: '/api/changeStyle'
};

export default app;