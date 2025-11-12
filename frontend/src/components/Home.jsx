import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CreateMeetingModal from './CreateMeetingModal.jsx';
import '../styles/Home.css';

function Home() {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [meetings, setMeetings] = useState([]);

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
        if (!token) {
            console.warn("Nema tokena, korisnik nije prijavljen.");
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/meetings/my', {
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (response.ok) {
                const data = await response.json();
                setMeetings(data);
            } else {
                console.error("Greška pri dohvaćanju sastanaka:", response.status);
            }
        } catch (err) {
            console.error("Greška:", err);
        }
    };

    useEffect(() => {
        fetchMeetings();
    }, []);

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

            const response = await fetch('http://localhost:8080/api/meetings', {
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


    return (
        <div className="home-container">
            <header className="home-header">
                <div className="header-content">
                    <h1 className="logo">StanPlan</h1>
                    <button className="btn-logout" onClick={handleLogout}>
                        Odjavi se
                    </button>
                </div>
            </header>

            <main className="home-main">
                <div className="content-wrapper">

                    <div className="action-cards">
                        <div className="action-card primary" onClick={handleCreateMeeting}>
                            <div className="card-icon">➕</div>
                            <h3>Kreiraj Sastanak</h3>
                            <p>Organiziraj novi sastanak stanara</p>
                        </div>
                    </div>

                </div>
                <div className="meetings-section">
                    <h2>Moji sastanci</h2>

                    {meetings.length === 0 ? (
                        <p>Nemaš još nijedan sastanak.</p>
                    ) : (
                        <ul className="meeting-list">
                            {meetings.map(meeting => (
                                <li key={meeting.id} className="meeting-item">
                                    <h3>{meeting.title}</h3>
                                    <p><strong>Lokacija:</strong> {meeting.location}</p>
                                    <p><strong>Vrijeme:</strong> {new Date(meeting.meetingDatetime).toLocaleString()}
                                    </p>
                                    <p><strong>Status:</strong> {meeting.state}</p>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>

            </main>

            {isModalOpen && (
                <CreateMeetingModal
                    onClose={handleCloseModal}
                    onSubmit={handleMeetingCreated}
                />
            )}
        </div>
    );
}

export default Home;