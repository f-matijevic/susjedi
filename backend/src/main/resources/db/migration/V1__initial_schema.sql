--enum za stanje sastanka
CREATE TYPE meeting_state AS ENUM ('PLANIRAN', 'OBJAVLJEN', 'OBAVLJEN', 'ARHIVIRAN');

--enum za rezultat glasanja
CREATE TYPE voting_result AS ENUM ('IZGLASAN', 'ODBIJEN');

--tablica korisnika
CREATE TABLE users (
	id BIGSERIAL PRIMARY KEY,
	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL UNIQUE,
	role VARCHAR(15) NOT NULL CHECK (role in ('ADMIN', 'SUVLASNIK', 'PREDSTAVNIK')),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--tablica sastanaka
CREATE TABLE meetings (
	id BIGSERIAL PRIMARY KEY,
	title VARCHAR(150) NOT NULL,
	summary TEXT NOT NULL,
	meeting_ts TIMESTAMP NOT NULL,
	location VARCHAR(150) NOT NULL,
	state meeting_state NOT NULL DEFAULT 'PLANIRAN',
	created_by BIGINT NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP,
	--linija ispod povezuje created_by tako da odgovara
	--nekom id-u iz tablice users
	--ako se taj korisnik obriše, brišu se njegovi sastanci
	CONSTRAINT fk_meeting_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

--tablica točaka dnevnog reda
CREATE TABLE agenda_items (
	id BIGSERIAL PRIMARY KEY,
	meeting_id BIGINT NOT NULL,
	title VARCHAR(150) NOT NULL,
	description TEXT,
	order_number INTEGER NOT NULL,
	has_legal_effect BOOLEAN NOT NULL DEFAULT FALSE,
	requires_voting BOOLEAN NOT NULL DEFAULT FALSE,
	stanblog_discussion_url VARCHAR(500),
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_agenda_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE,
	CONSTRAINT unique_meeting_order UNIQUE (meeting_id, order_number)	
);

-- tablica zaključaka
CREATE TABLE conclusions (
    id BIGSERIAL PRIMARY KEY,
    agenda_item_id BIGINT NOT NULL UNIQUE,
    content TEXT NOT NULL,
    voting_result voting_result,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conclusion_agenda FOREIGN KEY (agenda_item_id) REFERENCES agenda_items(id) ON DELETE CASCADE
);

-- tablica potvrda dolaska 
CREATE TABLE meeting_attendances (
    meeting_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    confirmed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (meeting_id, user_id),
    CONSTRAINT fk_attendance_meeting FOREIGN KEY (meeting_id) REFERENCES meetings(id) ON DELETE CASCADE,
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- konfiguracija sustava
CREATE TABLE system_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- indeksi
CREATE INDEX idx_meetings_state ON meetings(state);
CREATE INDEX idx_meetings_ts ON meetings(meeting_ts);
CREATE INDEX idx_meetings_creator ON meetings(created_by);
CREATE INDEX idx_agenda_items_meeting ON agenda_items(meeting_id);
CREATE INDEX idx_conclusions_agenda ON conclusions(agenda_item_id);
CREATE INDEX idx_attendances_meeting ON meeting_attendances(meeting_id);
CREATE INDEX idx_attendances_user ON meeting_attendances(user_id);

-- inicijalni podaci (admin)
-- pass: Admin123! (u produkciji će biti hashirana!)
INSERT INTO users (username, password, email, role) 
VALUES ('admin', '$2a$10$ifWDofwQHdjHbdAIwaEbJuuqZ9JLl9OiMlwPwuLM9/m.riHjwkese', 'admin@stanplan.com', 'ADMIN');

COMMENT ON TABLE users IS 'Korisnici sustava (admin, predstavnici, suvlasnici)';
COMMENT ON TABLE meetings IS 'Sastanci stanara';
COMMENT ON TABLE agenda_items IS 'Točke dnevnog reda sastanka';
COMMENT ON TABLE conclusions IS 'Zaključci točaka dnevnog reda';
COMMENT ON TABLE meeting_attendances IS 'Potvrde dolaska korisnika na sastanke';
COMMENT ON TABLE system_config IS 'Konfiguracija sustava';

COMMENT ON COLUMN meetings.state IS 'Stanje sastanka: PLANIRAN -> OBJAVLJEN -> OBAVLJEN -> ARHIVIRAN';
COMMENT ON COLUMN agenda_items.has_legal_effect IS 'Točka s pravnim učinkom?';
COMMENT ON COLUMN agenda_items.requires_voting IS 'Treba li glasovanje?';
COMMENT ON COLUMN conclusions.voting_result IS 'Rezultat glasovanja: IZGLASAN ili ODBIJEN';