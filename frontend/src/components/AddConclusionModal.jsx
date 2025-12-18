import React, { useState } from 'react';

function AddConclusionModal({ onClose, onSubmit, agendaItem }) {
    const [content, setContent] = useState("");
    const [votingResult, setVotingResult] = useState("IZGLASAN");

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!content.trim()) {
            alert("Molimo unesite tekst zaključka.");
            return;
        }
        onSubmit(agendaItem.id, content, agendaItem.hasLegalEffect ? votingResult : null);
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content" style={{ width: '500px' }}>
                <div className="modal-header">
                    <h2>Unos zaključka</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>

                <div style={{ marginBottom: '15px', color: '#666' }}>
                    <p><strong>Točka:</strong> {agendaItem.title}</p>
                </div>

                <form onSubmit={handleSubmit} className="form-container">
                    <div className="form-group">
                        <label>Tekst zaključka / Sažetak rasprave</label>
                        <textarea
                            required
                            rows="5"
                            style={{
                                padding: '10px',
                                borderRadius: '8px',
                                border: '1px solid #ddd',
                                fontFamily: 'inherit'
                            }}
                            placeholder="Upišite službeni zaključak..."
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                        />
                    </div>

                    {agendaItem.hasLegalEffect && (
                        <div className="form-group">
                            <label>Ishod glasanja</label>
                            <select
                                value={votingResult}
                                onChange={(e) => setVotingResult(e.target.value)}
                                style={{ padding: '10px', borderRadius: '8px', border: '1px solid #ddd' }}
                            >
                                <option value="IZGLASAN">✅ IZGLASAN</option>
                                <option value="ODBIJEN">❌ ODBIJEN</option>
                            </select>
                        </div>
                    )}

                    <div className="modal-actions">
                        <button type="submit" className="btn-submit">Spremi zaključak</button>
                        <button type="button" className="btn-cancel" onClick={onClose}>Odustani</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default AddConclusionModal;