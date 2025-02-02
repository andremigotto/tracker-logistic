ALTER TABLE parcels ADD COLUMN expired_at DATETIME DEFAULT NULL;
CREATE INDEX idx_parcel_expired ON parcels (expired_at);