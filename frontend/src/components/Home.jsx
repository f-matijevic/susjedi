import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CreateMeetingModal from './CreateMeetingModal.jsx';
import ChangePasswordModal from './ChangePasswordModal.jsx';
import AddAgendaItemModal from './AddAgendaItemModal.jsx';
import AddConclusionModal from './AddConclusionModal.jsx';
import '../styles/Home.css';

function Home() {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isPassModalOpen, setIsPassModalOpen] = useState(false);
    const [isAgendaModalOpen, setIsAgendaModalOpen] = useState(false);
    const [selectedMeetingId, setSelectedMeetingId] = useState(null);
    const [meetings, setMeetings] = useState([]);
    const [publishedMeetings, setPublishedMeetings] = useState([]);
    const [isConclusionModalOpen, setIsConclusionModalOpen] = useState(false);
    const [selectedAgendaItem, setSelectedAgendaItem] = useState(null);
    const API_URL = import.meta.env.VITE_API_URL;

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('role');
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
            const myRes = await fetch(`${API_URL}/api/meetings/my`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (myRes.ok) setMeetings(await myRes.json());

            const pubRes = await fetch(`${API_URL}/api/meetings/published`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (pubRes.ok) {
                const data = await pubRes.json();
                setPublishedMeetings(data);
                console.log("Published meetings:", data); // Debug log
            }

        } catch (err) {
            console.error("Greška pri dohvaćanju sastanaka:", err);
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

            if (!response.ok) {
                const responseText = await response.text();
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
        if (!window.confirm("Jeste li sigurni da želite objaviti sastanak?")) return;

        try {
            const response = await fetch(`${API_URL}/api/meetings/${meetingId}/publish`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                alert("Sastanak objavljen!");
                fetchMeetings();
            } else {
                const error = await response.text();
                alert("Greška: " + error);
            }
        } catch (err) {
            console.error("Greška pri objavi:", err);
        }
    };

    const handleConfirmAttendance = async (meetingId) => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`${API_URL}/api/meetings/${meetingId}/confirm`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                alert("Uspješno potvrđeno!");
                await fetchMeetings();
            } else {
                const errorText = await response.text();
                alert(errorText || "Greška pri potvrdi.");
                await fetchMeetings();
            }
        } catch (error) {
            console.error("Greška pri potvrdi dolaska:", error);
            alert("Problem s povezivanjem na server.");
        }
    };
    const handleAddConclusion = async (itemId, content, votingResult) => {
        if (!content) return alert("Molimo unesite tekst zaključka.");

        try {
            const token = localStorage.getItem('token');
            const response = await fetch(`${API_URL}/api/meetings/agenda-items/${itemId}/conclusion`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    content: content,
                    votingResult: votingResult
                })
            });

            if (response.ok) {
                alert("Zaključak uspješno dodan!");
                await fetchMeetings();
            } else {
                const errorMsg = await response.text();
                alert("Greška: " + errorMsg);
            }
        } catch (error) {
            console.error("Greška pri slanju zaključka:", error);
        }
    };
    const handleCompleteMeeting = async (meetingId) => {
        if (!window.confirm("Želite li završiti sastanak?")) return;

        try {
            const response = await fetch(`${API_URL}/api/meetings/${meetingId}/complete`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (response.ok) {
                alert("Sastanak završen! Sada možete unijeti zaključke u sekciji niže.");
                fetchMeetings();
            } else {
                const err = await response.text();
                alert("Greška: " + err);
            }
        } catch (err) {
            console.error("Greška pri završavanju sastanka:", err);
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
                                        <p> {meeting.location}</p>
                                        <p> {new Date(meeting.meetingDatetime).toLocaleString()}</p>
                                        <p className="attendance-count">
                                             Potvrđeno
                                            dolazaka: <strong>{meeting.attendeeUsernames?.length || 0}</strong>
                                        </p>
                                    </div>

                                    <div className="agenda-section">
                                        <h4>Dnevni red:</h4>
                                        <ul className="agenda-list">
                                            {meeting.agendaItems?.sort((a, b) => a.orderNumber - b.orderNumber).map(item => (
                                                <li key={item.id} className="agenda-item">
                                                    <strong>{item.orderNumber}. {item.title}</strong>
                                                    {item.hasLegalEffect &&
                                                        <span className="legal-badge">Pravni učinak</span>}
                                                    {item.description &&
                                                        <p className="agenda-desc">{item.description}</p>}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>

                                    <div className="attendance-actions" style={{marginTop: '15px'}}>
                                        {meeting.currentUserAttending ? (
                                            <span className="status-confirmed">
                                                 Potvrđen dolazak
                                            </span>
                                        ) : (
                                            <button
                                                className="btn-submit"
                                                onClick={() => handleConfirmAttendance(meeting.id)}
                                            >
                                                Potvrdi dolazak
                                            </button>
                                        )}
                                        {meeting.state === 'OBJAVLJEN' && (
                                            <button
                                                className="btn-submit"
                                                onClick={() => handleCompleteMeeting(meeting.id)}
                                            >
                                                Završi sastanak
                                            </button>
                                        )}
                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}

                    <div style={{margin: '40px 0'}}></div>

                    <h2>Moji sastanci (Planirani)</h2>
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
                                        <p> {meeting.location}</p>
                                        <p> {new Date(meeting.meetingDatetime).toLocaleString()}</p>
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
                    <h2>Sastanci za obradu (Obavljeni)</h2>
                    {meetings.filter(m => m.state === 'OBAVLJEN').length === 0 ? (
                        <p>Nema sastanaka koji čekaju zaključke.</p>
                    ) : (
                        <ul className="meeting-list">
                            {meetings.filter(m => m.state === 'OBAVLJEN').map(meeting => (
                                <li key={meeting.id} className="meeting-item">
                                    <div className="meeting-header">
                                        <h3>{meeting.title}</h3>
                                        <span className="status-badge obavljen">{meeting.state}</span>
                                    </div>

                                    <div className="agenda-section">
                                        <h4>Dnevni red i zaključci:</h4>
                                        <ul className="agenda-list">
                                            {meeting.agendaItems?.sort((a, b) => a.orderNumber - b.orderNumber).map(item => (
                                                <li key={item.id} className="agenda-item">
                                                    <strong>{item.orderNumber}. {item.title}</strong>
                                                    {item.hasLegalEffect && (
                                                        <span className="legal-badge">Pravni učinak</span>
                                                    )}
                                                    {item.conclusion ? (
                                                        <div className="conclusion-box">
                                                            <p>✅ <strong>Zaključak:</strong> {item.conclusion.content}</p>
                                                            {item.conclusion.votingResult && (
                                                                <span className={`status-badge ${item.conclusion.votingResult.toLowerCase()}`}>
                                                                    {item.conclusion.votingResult}
                                                                </span>
                                                            )}
                                                        </div>
                                                    ) : (
                                                        <div style={{ marginTop: '10px' }}>
                                                            <button
                                                                className="btn-add-agenda"
                                                                style={{ padding: '8px 15px', fontSize: '0.85em' }}
                                                                onClick={() => {
                                                                    setSelectedAgendaItem(item);
                                                                    setIsConclusionModalOpen(true);
                                                                }}
                                                            >
                                                                ➕ Dodaj zaključak
                                                            </button>
                                                        </div>
                                                    )}
                                                </li>
                                            ))}
                                        </ul>
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
            {isConclusionModalOpen && (
                <AddConclusionModal
                    agendaItem={selectedAgendaItem}
                    onClose={() => setIsConclusionModalOpen(false)}
                    onSubmit={async (itemId, content, result) => {
                        await handleAddConclusion(itemId, content, result);
                        setIsConclusionModalOpen(false);
                    }}
                />
            )}
        </div>
    );
}

export default Home;