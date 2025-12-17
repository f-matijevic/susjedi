import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CreateMeetingModal from './CreateMeetingModal.jsx';
import ChangePasswordModal from './ChangePasswordModal.jsx';
import AddAgendaItemModal from './AddAgendaItemModal.jsx';
import '../styles/Home.css';

function Home() {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isPassModalOpen, setIsPassModalOpen] = useState(false);
    const [isAgendaModalOpen, setIsAgendaModalOpen] = useState(false);
    const [selectedMeetingId, setSelectedMeetingId] = useState(null);
    const [meetings, setMeetings] = useState([]);
    const [publishedMeetings, setPublishedMeetings] = useState([]);
    const API_URL = import.meta.env.VITE_API_URL;

    const handleLogout = () => {
        localStorage.removeItem('token');
        navigate('/');
    };

    const handleCreateMeeting = () => {
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const fetchMeetings = async () => {
        const token = localStorage.getItem('token');
        if (!token) return;

        try {
            // 1. Dohvati moje sastanke (za predstavnika)
            const myRes = await fetch(`${API_URL}/api/meetings/my`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (myRes.ok) setMeetings(await myRes.json());

            // 2. Dohvati sve objavljene sastanke (za sve)
            const pubRes = await fetch(`${API_URL}/api/meetings/published`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (pubRes.ok) setPublishedMeetings(await pubRes.json());

        } catch (err) {
            console.error("Greška pri dohvaćanju:", err);
        }
    };

    useEffect(() => {
        fetchMeetings();
    }, []);

    const handleChangePassword = async (passData) => {
        const token = localStorage.getItem('token');
        try {
            const response = await fetch(`${API_URL}/api/users/change-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    oldPassword: passData.oldPassword,
                    newPassword: passData.newPassword
                })
            });

            if (response.ok) {
                alert("Lozinka uspješno promijenjena!");
                setIsPassModalOpen(false);
            } else {
                const errorText = await response.text();
                alert("Greška: " + errorText);
            }
        } catch (error) {
            alert("Problem s povezivanjem na server.");
        }
    };

    const handleMeetingCreated = async (meetingData) => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                alert("Niste prijavljeni ili je token istekao");
                return;
            }

            const payload = {
                title: meetingData.title,
                summary: meetingData.summary,
                meetingDatetime: new Date(meetingData.datetime).toISOString().replace(/\.\d{3}Z$/, '+00:00'),
                location: meetingData.location
            };

            const response = await fetch(`${API_URL}/api/meetings`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            console.log('Response status:', response.status);
            const responseText = await response.text();
            console.log('Response body:', responseText);

            if (!response.ok) {
                throw new Error(`Server error: ${response.status} - ${responseText}`);
            }

            alert('Sastanak uspješno kreiran!');
            setIsModalOpen(false);

            await fetchMeetings();

        } catch (error) {
            console.error('Error creating meeting:', error);
            alert('Greška: ' + error.message);
        }
    };
    const handleAddAgendaItem = async (meetingId, agendaData) => {
        try {
            const response = await fetch(`${API_URL}/api/meetings/${meetingId}/agenda-items`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(agendaData)
            });

            if (response.ok) {
                alert("Točka uspješno dodana!");
                setIsAgendaModalOpen(false);
                fetchMeetings();
            } else {
                const error = await response.text();
                alert("Greška: " + error);
            }
        } catch (err) {
            console.error("Greška pri dodavanju točke:", err);
        }
    };
    const handlePublishMeeting = async (meetingId) => {
        if (!window.confirm("Jeste li sigurni da želite objaviti sastanak? Nakon objave više ne možete dodavati točke.")) return;

        try {
            const response = await fetch(`${API_URL}/api/meetings/${meetingId}/publish`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                alert("Sastanak objavljen!");
                fetchMeetings(); // Osvježi listu
            } else {
                const error = await response.text();
                alert("Greška: " + error);
            }
        } catch (err) {
            console.error("Greška pri objavi:", err);
        }
    };
    return (
        <div className="home-container">
            <header className="home-header">
                <div className="header-content">
                    <h1 className="logo">StanPlan</h1>
                    <div className="header-buttons">
                        <button className="btn-change-pass" onClick={() => setIsPassModalOpen(true)}>
                            Promijeni zaporku
                        </button>
                        <button className="btn-admin" onClick={() => navigate('/SignUp')}>
                            Registriraj (Admin)
                        </button>
                        <button className="btn-logout" onClick={handleLogout}>
                            Odjavi se
                        </button>
                    </div>
                </div>
            </header>

            <main className="home-main">
                <button className="btn-submit" onClick={handleCreateMeeting} style={{maxWidth: '200px', marginBottom: '20px'}}>
                    ➕ Kreiraj Sastanak
                </button>

                <div className="meetings-section">
                    <h2>Objavljeni sastanci</h2>
                    {publishedMeetings.length === 0 ? (
                        <p>Trenutno nema objavljenih sastanaka.</p>
                    ) : (
                        <ul className="meeting-list">
                            {publishedMeetings.map(meeting => (
                                <li key={meeting.id} className="meeting-item">
                                    <div className="meeting-header">
                                        <h3>{meeting.title}</h3>
                                        <span className="status-badge objavljen">{meeting.state}</span>
                                    </div>
                                    <div className="meeting-details">
                                        <p>{meeting.location}</p>
                                        <p>{new Date(meeting.meetingDatetime).toLocaleString()}</p>
                                    </div>

                                    <div className="agenda-section">
                                        <h4>Dnevni red:</h4>
                                        <ul className="agenda-list">
                                            {meeting.agendaItems?.sort((a, b) => a.orderNumber - b.orderNumber).map(item => (
                                                <li key={item.id} className="agenda-item">
                                                    <strong>{item.orderNumber}. {item.title}</strong>
                                                    {item.hasLegalEffect && <span className="legal-badge">Pravni učinak</span>}
                                                    {item.description && <p className="agenda-desc">{item.description}</p>}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}

                    <div style={{margin: '40px 0'}}></div>

                    <h2>Moji sastanci</h2>
                    {meetings.filter(m => m.state === 'PLANIRAN').length === 0 ? (
                        <p>Nemaš sastanaka u pripremi.</p>
                    ) : (
                        <ul className="meeting-list">
                            {meetings.filter(m => m.state === 'PLANIRAN').map(meeting => (
                                <li key={meeting.id} className="meeting-item">
                                    <div className="meeting-header">
                                        <h3>{meeting.title}</h3>
                                        <span className="status-badge planiran">{meeting.state}</span>
                                    </div>
                                    <div className="meeting-details">
                                        <p>{meeting.location}</p>
                                        <p>{new Date(meeting.meetingDatetime).toLocaleString()}</p>
                                    </div>

                                    <div className="agenda-section">
                                        <h4>Dnevni red:</h4>
                                        {meeting.agendaItems && meeting.agendaItems.length > 0 ? (
                                            <ul className="agenda-list">
                                                {meeting.agendaItems.sort((a, b) => a.orderNumber - b.orderNumber).map(item => (
                                                    <li key={item.id} className="agenda-item">
                                                        <strong>{item.orderNumber}. {item.title}</strong>
                                                        {item.hasLegalEffect && <span className="legal-badge">Pravni učinak</span>}
                                                    </li>
                                                ))}
                                            </ul>
                                        ) : <p className="no-agenda">Nema dodanih točaka.</p>}
                                    </div>
                                    <div className="modal-actions">
                                        <button className="btn-add-agenda" onClick={() => {
                                            setSelectedMeetingId(meeting.id);
                                            setIsAgendaModalOpen(true);
                                        }}>
                                            + Dodaj točku
                                        </button>

                                        {meeting.agendaItems?.length > 0 && (
                                            <button className="btn-submit" onClick={() => handlePublishMeeting(meeting.id)} style={{padding: '5px 10px'}}>
                                                Objavi sastanak
                                            </button>
                                        )}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </main>

            {isPassModalOpen && (
                <ChangePasswordModal onClose={() => setIsPassModalOpen(false)} onSubmit={handleChangePassword} />
            )}
            {isModalOpen && (
                <CreateMeetingModal onClose={handleCloseModal} onSubmit={handleMeetingCreated} />
            )}
            {isAgendaModalOpen && (
                <AddAgendaItemModal
                    isOpen={isAgendaModalOpen}
                    onClose={() => setIsAgendaModalOpen(false)}
                    onSubmit={handleAddAgendaItem}
                    meetingId={selectedMeetingId}
                />
            )}
        </div>
    );
}

export default Home;