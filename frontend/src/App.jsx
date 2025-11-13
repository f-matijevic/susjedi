import { HashRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Home from "./components/Home"
import OAuth2Callback from "./components/OAuth2Callback";
import ProtectedRoute from "./services/protectedroute.jsx";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Login />} />

                <Route path="/signup" element={<SignUp />} />

                <Route path="/auth/callback" element={<OAuth2Callback />} />

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