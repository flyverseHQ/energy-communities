package at.fhtw.disys.percentage.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class PercentageRepository {

    private final JdbcTemplate jdbcTemplate;

    public PercentageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertCurrentPercentage(
            LocalDateTime hour,
            double communityDepleted,
            double gridPortion
    ) {
        String sql = """
                INSERT INTO current_percentage (
                    hour,
                    community_depleted,
                    grid_portion,
                    updated_at
                )
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                ON CONFLICT (hour)
                DO UPDATE SET
                    community_depleted = EXCLUDED.community_depleted,
                    grid_portion = EXCLUDED.grid_portion,
                    updated_at = CURRENT_TIMESTAMP
                """;

        jdbcTemplate.update(
                sql,
                hour,
                communityDepleted,
                gridPortion
        );
    }
}