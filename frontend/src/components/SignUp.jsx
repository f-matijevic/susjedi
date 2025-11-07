import React, { useState } from "react";
import "../styles/SignUp.css";
import userPicture from "../assets/userPicture.svg";
import googleIcon from "../assets/googleIcon.png";
import { Link } from "react-router-dom";

export default function SignUp(){
    
     const handleSubmit = (e) => {
        e.preventDefault();
        console.log("Pokušaj prijave!");
    };

    return (
        <div className="LoginPage">
                <h1 id="Naslov">SUSJEDI</h1>
            <div className="LoginContainer">
                <form className="formObrazac" onSubmit={handleSubmit}>
                    <label htmlFor="Name">Ime</label>
                    <input type="text"
                           placeholder="Hrvoje"
                            id="Name"
                            required
                    ></input>
                    <label htmlFor="SurName">Prezime</label>
                    <input type="text"
                           placeholder="Horvat"
                            id="SurName"
                            required
                    ></input>
                    <label htmlFor="email">Email</label>
                    <input type="email"
                           placeholder="hrvoje.horvat@gmail.com"
                            id="email"
                            required
                    ></input>
                    <label htmlFor="lozinka">Lozinka</label>
                    <input
                        type="password"
                        id="lozinka"
                        placeholder="*******"
                        required
                        minLength="8"
                        pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$"
                        title="min 8 znakova, jedno veliko slovo, broj i poseban znak (!@#$%^&*)."
                    />
                    <button type="submit" className="LoginButton">
                        Registriraj se
                    </button>
                </form> 
                
                <div className="LoginGoogle">
                    <img    id="googleImage"
                            src={googleIcon}
                            alt="Google"
                    />
                    <button className="google-button">
                        Registrirajte se pomoću Google računa
                    </button>
                </div>
            </div>
        </div>

    );
}
