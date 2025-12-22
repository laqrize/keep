CREATE TABLE IF NOT EXISTS labels
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) UNIQUE NOT NULL,
    status        VARCHAR(3)   NOT NULL DEFAULT 'C',
    created_at    TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_at    TIMESTAMP,
    updated_by    BIGINT,
    version       BIGINT,

    CONSTRAINT fk_labels_created_by FOREIGN KEY (created_by)
        REFERENCES app_users(id),

    CONSTRAINT fk_labels_updated_by
        FOREIGN KEY (updated_by)
            REFERENCES app_users (id)

);

CREATE UNIQUE INDEX IF NOT EXISTS uq_labels_email_active
    ON labels (name, created_by)
    WHERE status = 'A';


