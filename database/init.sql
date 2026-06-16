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
      ('2025-01-09 09:00:00', 38.500, 38.500, 21.400),
      ('2025-01-09 10:00:00', 64.200, 58.900, 8.300),
      ('2025-01-09 11:00:00', 91.750, 79.400, 3.600),
      ('2025-01-09 12:00:00', 116.300, 102.500, 2.800),
      ('2025-01-09 13:00:00', 132.800, 118.900, 4.500),
      ('2025-01-09 14:00:00', 146.600, 134.200, 5.900),
      ('2025-01-09 15:00:00', 139.400, 132.700, 10.100),
      ('2025-01-09 16:00:00', 111.900, 111.900, 25.600),
      ('2025-01-09 17:00:00', 75.200, 75.200, 48.700),
      ('2025-01-09 18:00:00', 32.800, 32.800, 66.400),

      ('2025-01-10 09:00:00', 45.200, 45.200, 18.400),
      ('2025-01-10 10:00:00', 72.800, 68.300, 6.700),
      ('2025-01-10 11:00:00', 98.400, 87.100, 2.300),
      ('2025-01-10 12:00:00', 112.300, 100.200, 5.400),
      ('2025-01-10 13:00:00', 120.500, 110.300, 10.200),
      ('2025-01-10 14:00:00', 143.024, 130.101, 14.750),
      ('2025-01-10 15:00:00', 160.000, 152.000, 7.500),
      ('2025-01-10 16:00:00', 150.000, 145.000, 12.000),
      ('2025-01-10 17:00:00', 120.000, 120.000, 30.000),
      ('2025-01-10 18:00:00', 80.000, 80.000, 45.000)
    ON CONFLICT (hour) DO UPDATE SET
    community_produced = EXCLUDED.community_produced,
                              community_used = EXCLUDED.community_used,
                              grid_used = EXCLUDED.grid_used,
                              updated_at = CURRENT_TIMESTAMP;

INSERT INTO current_percentage (
    hour,
    community_depleted,
    grid_portion
)
SELECT
    hour,
    CASE
    WHEN community_produced <= 0 THEN 0
    ELSE ROUND(((community_used / community_produced) * 100)::numeric, 2)::double precision
END AS community_depleted,
    CASE
        WHEN (community_used + grid_used) <= 0 THEN 0
        ELSE ROUND(((grid_used / (community_used + grid_used)) * 100)::numeric, 2)::double precision
END AS grid_portion
FROM energy_usage
WHERE hour >= '2025-01-09 00:00:00'
  AND hour <= '2025-01-10 23:59:59'
ON CONFLICT (hour) DO UPDATE SET
    community_depleted = EXCLUDED.community_depleted,
                              grid_portion = EXCLUDED.grid_portion,
                              updated_at = CURRENT_TIMESTAMP;