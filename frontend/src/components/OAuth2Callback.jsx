import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";

export default function OAuth2Callback() {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const token = params.get("token");

        if (token) {
            localStorage.setItem("token", token);
            navigate("/home");
        } else {
            navigate("/");
        }
    }, [location, navigate]);

    return <div>Logging in...</div>;
}
