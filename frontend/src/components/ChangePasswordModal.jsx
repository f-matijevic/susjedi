import React, { useState } from 'react';

function ChangePasswordModal({ onClose, onSubmit }) {
    const [passData, setPassData] = useState({
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        if (passData.newPassword !== passData.confirmPassword) {
            alert("Nove lozinke se ne podudaraju!");
            return;
        }
        onSubmit(passData);
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Sigurnost računa</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>

                <form onSubmit={handleSubmit} className="form-container">
                    <div className="form-group">
                        <label>Trenutna lozinka</label>
                        <input
                            type="password"
                            required
                            onChange={(e) => setPassData({...passData, oldPassword: e.target.value})}
                        />
                    </div>

                    <div className="form-group">
                        <label>Nova lozinka</label>
                        <input
                            type="password"
                            required
                            minLength="8"
                            onChange={(e) => setPassData({...passData, newPassword: e.target.value})}
                        />
                    </div>

                    <div className="form-group">
                        <label>Potvrdi novu lozinku</label>
                        <input
                            type="password"
                            required
                            onChange={(e) => setPassData({...passData, confirmPassword: e.target.value})}
                        />
                    </div>

                    <div className="modal-actions">
                        <button type="submit" className="btn-submit">Spremi promjene</button>
                        <button type="button" className="btn-cancel" onClick={onClose}>Odustani</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default ChangePasswordModal;