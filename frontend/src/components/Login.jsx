import { Link } from "react-router-dom";
import React, { useState } from "react";
import "../styles/Login.css";
import userPicture from "../assets/userPicture.svg";
import googleIcon from "../assets/googleIcon.png";

export default function Login(){
    
     const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Pokušaj prijave!");
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
                    ></input>
                    <label htmlFor="lozinka">Lozinka:</label>
                    <input
                        type="password"
                        id="lozinka"
                        placeholder="*******"
                        required
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
                    <img    id="googleImage"
                            src={googleIcon}
                            alt="Google"
                    />
                    <button className="google-button">
                        Prijavite se pomoću Google računa
                    </button>
                </div>
            </div>
        </div>

    );
}