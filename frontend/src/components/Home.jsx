import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CreateMeetingModal from './CreateMeetingModal.jsx';
import '../styles/Home.css';

function Home() {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false);

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

            const created = JSON.parse(responseText);
            console.log('Meeting created:', created);
            alert('Sastanak uspješno kreiran!');
            setIsModalOpen(false);

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