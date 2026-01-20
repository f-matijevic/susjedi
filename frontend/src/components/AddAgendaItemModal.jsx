import React, { useState } from 'react';

const AddAgendaItemModal = ({ isOpen, onClose, onSubmit, meetingId, stanBlogDiscussions }) => {
    const [itemData, setItemData] = useState({
        title: '',
        description: '',
        orderNumber: 1,
        hasLegalEffect: false,
        requiresVoting: false,
        stanBlogDiscussionUrl: ''
    });

    if (!isOpen) return null;

    const handleSelectDiscussion = (disc) => {
        setItemData({
            ...itemData,
            title: disc.naslov,
            description: disc.pitanje || '',
            stanblogDiscussionUrl: `https://stanblog.onrender.com${disc.poveznica}`,
            hasLegalEffect: true,
            requiresVoting: true
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(meetingId, itemData);
        onClose();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content" style={{maxWidth: '800px'}}>
                <div className="modal-header">
                    <h2>Nova točka dnevnog reda</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>

                <div className="modal-body" style={{display: 'flex', gap: '20px'}}>
                    <div className="stanblog-sidebar" style={{flex: 1, borderRight: '1px solid #eee', paddingRight: '15px'}}>
                        <h4>Preuzmi sa StanBloga</h4>
                        {stanBlogDiscussions?.length > 0 ? (
                            <ul style={{listStyle: 'none', padding: 0}}>
                                {stanBlogDiscussions.map((disc, idx) => (
                                    <li key={idx}
                                        onClick={() => handleSelectDiscussion(disc)}
                                        style={{
                                            padding: '10px',
                                            marginBottom: '5px',
                                            background: '#f8f9fa',
                                            cursor: 'pointer',
                                            borderRadius: '4px',
                                            fontSize: '0.9rem'
                                        }}>
                                        <strong>{disc.naslov}</strong>
                                    </li>
                                ))}
                            </ul>
                        ) : <p style={{fontSize: '0.8rem'}}>Nema dostupnih diskusija.</p>}
                    </div>

                    <form onSubmit={handleSubmit} style={{flex: 2}}>
                        <div className="form-group">
                            <label>Naslov točke</label>
                            <input
                                type="text"
                                required
                                value={itemData.title}
                                onChange={(e) => setItemData({...itemData, title: e.target.value})}
                            />
                        </div>
                        <div className="form-group">
                            <label>Opis / Bilješke</label>
                            <textarea
                                value={itemData.description}
                                onChange={(e) => setItemData({...itemData, description: e.target.value})}
                            />
                        </div>
                        {itemData.stanblogDiscussionUrl && (
                             <div className="form-group">
                                <label>Izvorna diskusija</label>
                                <input type="text" disabled value={itemData.stanblogDiscussionUrl} style={{fontSize: '0.7rem'}}/>
                             </div>
                        )}
                        <div className="checkbox-group">
                            <label>
                                <input
                                    type="checkbox"
                                    checked={itemData.hasLegalEffect}
                                    onChange={(e) => setItemData({...itemData, hasLegalEffect: e.target.checked})}
                                /> Pravni učinak
                            </label>
                            <label>
                                <input
                                    type="checkbox"
                                    checked={itemData.requiresVoting}
                                    onChange={(e) => setItemData({...itemData, requiresVoting: e.target.checked})}
                                /> Potrebno glasanje
                            </label>
                        </div>
                        <div className="modal-actions">
                            <button type="submit" className="btn-submit">Dodaj točku</button>
                            <button type="button" className="btn-cancel" onClick={onClose}>Odustani</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AddAgendaItemModal;