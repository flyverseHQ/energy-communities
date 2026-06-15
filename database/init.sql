CREATE TABLE IF NOT EXISTS energy_usage (
                                            hour TIMESTAMP PRIMARY KEY,
                                            community_produced DOUBLE PRECISION NOT NULL DEFAULT 0,
                                            community_used DOUBLE PRECISION NOT NULL DEFAULT 0,
                                            grid_used DOUBLE PRECISION NOT NULL DEFAULT 0,
                                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS current_percentage (
                                                  hour TIMESTAMP PRIMARY KEY,
                                                  community_depleted DOUBLE PRECISION NOT NULL DEFAULT 0,
                                                  grid_portion DOUBLE PRECISION NOT NULL DEFAULT 0,
                                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO energy_usage (
    hour,
    community_produced,
    community_used,
    grid_used
) VALUES
      ('2025-01-10 14:00:00', 143.024, 130.101, 14.75),
      ('2025-01-10 13:00:00', 120.500, 110.300, 10.20)
    ON CONFLICT (hour) DO NOTHING;

INSERT INTO current_percentage (
    hour,
    community_depleted,
    grid_portion
) VALUES
      ('2025-01-10 14:00:00', 90.97, 10.18),
      ('2025-01-10 13:00:00', 91.54, 8.46)
    ON CONFLICT (hour) DO NOTHING;