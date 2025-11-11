import React, { useState } from 'react';
import '../styles/CreateMeetingModal.css';

function CreateMeetingModal({ onClose, onSubmit }) {
    const [formData, setFormData] = useState({
        title: '',
        summary: '',
        datetime: '',
        location: ''
    });

    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const {name, value} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        if (errors[name]) {
            setErrors(prev => ({...prev, [name]: ''}));
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!formData.title.trim()) {
            newErrors.title = 'Naslov je obavezan';
        } else if (formData.title.length > 200) {
            newErrors.title = 'Naslov može imati max 200 znakova';
        }

        if (!formData.summary.trim()) {
            newErrors.summary = 'Sažetak je obavezan';
        }

        if (!formData.datetime) {
            newErrors.datetime = 'Datum i vrijeme su obavezni';
        }

        if (!formData.location.trim()) {
            newErrors.location = 'Lokacija je obavezna';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (validate()) {
            onSubmit(formData);
        }
    };

    const handleBackdropClick = (e) => {
        if (e.target.className === 'modal-backdrop') {
            onClose();
        }
    };

    return (
        <div className="modal-backdrop" onClick={handleBackdropClick}>
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Kreiraj Novi Sastanak</h2>
                    <button className="modal-close" onClick={onClose}>
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="modal-body">
                    <div className="form-group">
                        <label htmlFor="title">
                            Naslov <span className="required">*</span>
                        </label>
                        <input
                            type="text"
                            id="title"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            placeholder="Npr. Godišnji sastanak stanara 2025"
                            className={errors.title ? 'error' : ''}
                        />
                        {errors.title && <span className="error-text">{errors.title}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="summary">
                            Sažetak <span className="required">*</span>
                        </label>
                        <textarea
                            id="summary"
                            name="summary"
                            value={formData.summary}
                            onChange={handleChange}
                            rows={4}
                            placeholder="Opišite namjenu i teme sastanka..."
                            className={errors.summary ? 'error' : ''}
                        />
                        {errors.summary && <span className="error-text">{errors.summary}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="datetime">
                            Datum i vrijeme <span className="required">*</span>
                        </label>
                        <input
                            type="datetime-local"
                            id="datetime"
                            name="datetime"
                            value={formData.datetime}
                            onChange={handleChange}
                            className={errors.datetime ? 'error' : ''}
                        />
                        {errors.datetime && <span className="error-text">{errors.datetime}</span>}
                    </div>

                    <div className="form-group">
                        <label htmlFor="location">
                            Lokacija <span className="required">*</span>
                        </label>
                        <input
                            type="text"
                            id="location"
                            name="location"
                            value={formData.location}
                            onChange={handleChange}
                            placeholder="Npr. Zgrada A, prizemlje"
                            className={errors.location ? 'error' : ''}
                        />
                        {errors.location && <span className="error-text">{errors.location}</span>}
                    </div>

                    <div className="modal-footer">
                        <button type="button" className="btn-secondary" onClick={onClose}>
                            Odustani
                        </button>
                        <button type="submit" className="btn-primary">
                            Kreiraj Sastanak
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateMeetingModal;