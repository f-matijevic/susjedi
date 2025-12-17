import React, { useState } from "react";
import "../styles/SignUp.css";

export default function SignUp(){
    const API_URL = import.meta.env.VITE_API_URL;
    const [formData, setFormData] = useState({
        Name: "",
        email: "",
        lozinka: "",
        role: ""
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const token = localStorage.getItem("token");

        try {
            const response = await fetch(`${API_URL}/api/register`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(formData),
            });

            const data = await response.json();

            if (response.ok) {
                alert("Uspjeh: " + (data.message || data.success));
                setFormData({ Name: "", email: "", lozinka: "", role: "" });
            } else {
                alert("Greška: " + (data.message || data.error));
            }

        } catch (error) {
            console.error("Greška:", error);
            alert("Došlo je do greške pri komunikaciji sa serverom.");
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
                    <label htmlFor="role">Uloga</label>
                    <select id="role" required value={formData.role} onChange={handleChange}>
                        <option value="" disabled>-- Odaberite ulogu --</option>
                        <option value="SUVLASNIK">Suvlasnik</option>
                        <option value="PREDSTAVNIK">Predstavnik suvlasnika</option>
                    </select>
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
                        Registriraj račun
                    </button>
                </form>
            </div>
        </div>

    );
}
