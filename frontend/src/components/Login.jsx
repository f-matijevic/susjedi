import { Link, useNavigate } from "react-router-dom";
import React, { useState } from "react";
import "../styles/Login.css";
import userPicture from "../assets/userPicture.svg";
import googleIcon from "../assets/googleIcon.png";

export default function Login(){

    const [formData, setFormData] = useState({
        email: "",
        lozinka: ""
    });

    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/api/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });
            const data = await response.json();
            console.log(data.message);

            if (data.token) {
                localStorage.setItem("token", data.token);
                navigate("/home");
            } else {
                alert(data.message);
            }

        } catch (error) {
            console.error("Greška:", error);
        }
    };

    return (
        <div className="LoginPage">
                <h1 id="Naslov">SUSJEDI</h1>
            <div className="LoginContainer">
                <div className="login-avatar"><img src={userPicture} alt="UserPhoto"/></div>
                <form className="formObrazac" onSubmit={handleSubmit}>
                    <label htmlFor="email">Email</label>
                    <input type="email"
                           placeholder="ime.prezime@gmail.com"
                            id="email"
                            required
                            onChange={handleChange}
                    ></input>
                    <label htmlFor="lozinka">Lozinka:</label>
                    <input
                        type="password"
                        id="lozinka"
                        placeholder="*******"
                        required
                        onChange={handleChange}
                    />
                    <button type="submit" className="LoginButton">
                        Prijavi se
                    </button>
                </form> 
                <p className="Ulogiranje">
                    Nemate račun? 
                    <Link to="/SignUp"> Registrirajte se.</Link>
                </p>
                <div className="LoginGoogle">
                    <img id="googleImage" src={googleIcon} alt="Google"/>
                    <button
                        className="google-button"
                        onClick={() => {
                            window.location.href = "http://localhost:8080/oauth2/authorization/google";
                        }}
                    >
                        Prijavite se pomoću Google računa
                    </button>
                </div>
            </div>
        </div>

    );
}