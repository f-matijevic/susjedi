import React, { useState } from 'react';

const AddAgendaItemModal = ({ isOpen, onClose, onSubmit, meetingId }) => {
    const [itemData, setItemData] = useState({
        title: '',
        description: '',
        orderNumber: 1,
        hasLegalEffect: false,
        requiresVoting: false,
        stanblogDiscussionUrl: ''
    });

    if (!isOpen) return null;

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(meetingId, itemData);
        setItemData({ title: '', description: '', orderNumber: 1, hasLegalEffect: false, requiresVoting: false, stanblogDiscussionUrl: '' });
        onClose();
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Nova točka dnevnog reda</h2>
                    <button className="close-button" onClick={onClose}>&times;</button>
                </div>
                <form onSubmit={handleSubmit}>
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
                    <div className="form-group">
                        <label>Redni broj</label>
                        <input
                            type="number"
                            required
                            value={itemData.orderNumber}
                            onChange={(e) => setItemData({...itemData, orderNumber: parseInt(e.target.value)})}
                        />
                    </div>
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
    );
};

export default AddAgendaItemModal;