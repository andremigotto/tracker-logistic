CREATE TABLE parcels (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    description VARCHAR(255) NOT NULL,
    sender VARCHAR(255) NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    delivered_at DATETIME NULL
);

CREATE TABLE tracking_events (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    parcel_id BINARY(16) NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    date_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE CASCADE
);

CREATE TABLE dispatches (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    is_holiday BOOLEAN NOT NULL,
    fun_fact VARCHAR(500),
    estimated_delivery_date DATE,
    parcel_id BINARY(16) NOT NULL,
    CONSTRAINT fk_dispatch_parcel FOREIGN KEY (parcel_id) REFERENCES parcels(id) ON DELETE CASCADE
);
