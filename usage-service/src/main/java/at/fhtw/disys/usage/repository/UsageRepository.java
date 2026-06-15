package at.fhtw.disys.usage.repository;

import at.fhtw.disys.usage.dto.UsageUpdateMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class UsageRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addProducedEnergy(LocalDateTime hour, double kwh) {
        jdbcTemplate.update("""
                INSERT INTO energy_usage (hour, community_produced, community_used, grid_used, updated_at)
                VALUES (?, ?, 0, 0, CURRENT_TIMESTAMP)
                ON CONFLICT (hour)
                DO UPDATE SET
                    community_produced = energy_usage.community_produced + EXCLUDED.community_produced,
                    updated_at = CURRENT_TIMESTAMP
                """,
                Timestamp.valueOf(hour),
                kwh
        );
    }

    public UsageUpdateMessage addUsedEnergy(LocalDateTime hour, double userKwh) {
        ensureHourExists(hour);

        UsageUpdateMessage current = findByHourForUpdate(hour);

        double availableCommunityEnergy = current.getCommunityProduced() - current.getCommunityUsed();
        double communityIncrement = Math.min(userKwh, Math.max(availableCommunityEnergy, 0));
        double gridIncrement = userKwh - communityIncrement;

        double newCommunityUsed = current.getCommunityUsed() + communityIncrement;
        double newGridUsed = current.getGridUsed() + gridIncrement;

        jdbcTemplate.update("""
                UPDATE energy_usage
                SET community_used = ?,
                    grid_used = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE hour = ?
                """,
                newCommunityUsed,
                newGridUsed,
                Timestamp.valueOf(hour)
        );

        return new UsageUpdateMessage(
                hour,
                current.getCommunityProduced(),
                newCommunityUsed,
                newGridUsed
        );
    }

    public UsageUpdateMessage findByHour(LocalDateTime hour) {
        return jdbcTemplate.queryForObject("""
                SELECT hour, community_produced, community_used, grid_used
                FROM energy_usage
                WHERE hour = ?
                """,
                (rs, rowNum) -> new UsageUpdateMessage(
                        rs.getTimestamp("hour").toLocalDateTime(),
                        rs.getDouble("community_produced"),
                        rs.getDouble("community_used"),
                        rs.getDouble("grid_used")
                ),
                Timestamp.valueOf(hour)
        );
    }

    private void ensureHourExists(LocalDateTime hour) {
        jdbcTemplate.update("""
                INSERT INTO energy_usage (hour, community_produced, community_used, grid_used, updated_at)
                VALUES (?, 0, 0, 0, CURRENT_TIMESTAMP)
                ON CONFLICT (hour) DO NOTHING
                """,
                Timestamp.valueOf(hour)
        );
    }

    private UsageUpdateMessage findByHourForUpdate(LocalDateTime hour) {
        return jdbcTemplate.queryForObject("""
                SELECT hour, community_produced, community_used, grid_used
                FROM energy_usage
                WHERE hour = ?
                FOR UPDATE
                """,
                (rs, rowNum) -> new UsageUpdateMessage(
                        rs.getTimestamp("hour").toLocalDateTime(),
                        rs.getDouble("community_produced"),
                        rs.getDouble("community_used"),
                        rs.getDouble("grid_used")
                ),
                Timestamp.valueOf(hour)
        );
    }
}
