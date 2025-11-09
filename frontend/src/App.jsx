import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Home from "./components/Home"
import ProtectedRoute from "./services/protectedroute.jsx";

function App() {
  return (
    <Router>
        <Routes>
            <Route path="/" element={<Login />} />

            <Route path="/signup" element={<SignUp />} />

            <Route
                path="/home"
                element={
                    <ProtectedRoute>
                        <Home />
                    </ProtectedRoute>
                }
            />
        </Routes>
    </Router>
  );
}

export default App;
