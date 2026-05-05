CREATE DATABASE energy_communities;

\connect energy_communities;

CREATE TABLE IF NOT EXISTS energy_usage (
                                            hour TIMESTAMP PRIMARY KEY,
                                            community_produced DOUBLE PRECISION NOT NULL,
                                            community_used DOUBLE PRECISION NOT NULL,
                                            grid_used DOUBLE PRECISION NOT NULL,
                                            community_depleted DOUBLE PRECISION NOT NULL,
                                            grid_portion DOUBLE PRECISION NOT NULL
);

INSERT INTO energy_usage (
    hour,
    community_produced,
    community_used,
    grid_used,
    community_depleted,
    grid_portion
) VALUES
      ('2025-01-10 14:00:00', 143.024, 130.101, 14.75, 90.97, 10.18),
      ('2025-01-10 13:00:00', 120.500, 110.300, 10.20, 91.54, 8.46)
    ON CONFLICT (hour) DO NOTHING;