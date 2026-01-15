ALTER TABLE meeting_attendances DROP CONSTRAINT IF EXISTS meeting_attendances_pkey;
ALTER TABLE meeting_attendances ADD COLUMN id BIGSERIAL PRIMARY KEY;