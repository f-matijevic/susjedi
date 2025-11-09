import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Home.css";

export default function Home() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/");
    };
    return (
        <div className="HomePage">
            <h1 id="Naslov">SUSJEDI</h1>
            <button onClick={handleLogout} className="LogoutButton">
                Odjavi se
            </button>
        </div>

    );
}