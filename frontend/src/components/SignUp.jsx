import React, { useState } from "react";
import "../styles/SignUp.css";
import googleIcon from "../assets/googleIcon.png";

export default function SignUp(){

    const [formData, setFormData] = useState({
        Name: "",
        email: "",
        lozinka: ""
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            const data = await response.json();
            console.log(data);

        } catch (error) {
            console.error("Greška:", error);
        }
    };

    return (
        <div className="LoginPage">
                <h1 id="Naslov">SUSJEDI</h1>
            <div className="LoginContainer">
                <form className="formObrazac" onSubmit={handleSubmit}>
                    <label htmlFor="Name">Korisničko ime</label>
                    <input type="text"
                           placeholder="HrvojeHorvat"
                            id="Name"
                            required
                            onChange={handleChange}
                    ></input>
                    <label htmlFor="email">Email</label>
                    <input type="email"
                           placeholder="hrvoje.horvat@gmail.com"
                            id="email"
                            required
                            onChange={handleChange}
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
                        onChange={handleChange}
                    />
                    <button type="submit" className="LoginButton">
                        Registriraj se
                    </button>
                </form>

                <div className="LoginGoogle">
                    <img
                        id="googleImage"
                        src={googleIcon}
                        alt="Google"
                    />
                    <button
                        className="google-button"
                        onClick={() => {
                            window.location.href = "http://localhost:8080/oauth2/authorization/google";
                        }}
                    >
                        Registrirajte se pomoću Google računa
                    </button>
                </div>
            </div>
        </div>

    );
}
