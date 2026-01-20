ALTER TABLE meetings
    ADD COLUMN IF NOT EXISTS stanblog_discussion_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS created_from_stanblog BOOLEAN DEFAULT FALSE;

ALTER TABLE agenda_items
    ADD COLUMN IF NOT EXISTS stanblog_discussion_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS voting_question TEXT;

CREATE INDEX IF NOT EXISTS idx_meetings_created_from_stanblog
    ON meetings(created_from_stanblog) WHERE created_from_stanblog = true;

