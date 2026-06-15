package at.fhtw.disys.energy.repository;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EnergyRepository {

    private final JdbcTemplate jdbcTemplate;

    public EnergyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<CurrentEnergyDto> findCurrentEnergy() {
        String sql = """
                SELECT
                    eu.hour,
                    eu.community_produced,
                    eu.community_used,
                    eu.grid_used,
                    COALESCE(
                        cp.community_depleted,
                        CASE
                            WHEN eu.community_produced = 0 THEN 0
                            ELSE (eu.community_used / eu.community_produced) * 100
                        END
                    ) AS community_depleted,
                    COALESCE(
                        cp.grid_portion,
                        CASE
                            WHEN (eu.community_used + eu.grid_used) = 0 THEN 0
                            ELSE (eu.grid_used / (eu.community_used + eu.grid_used)) * 100
                        END
                    ) AS grid_portion
                FROM energy_usage eu
                LEFT JOIN current_percentage cp ON eu.hour = cp.hour
                ORDER BY eu.hour DESC
                LIMIT 1
                """;

        List<CurrentEnergyDto> result = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new CurrentEnergyDto(
                        rs.getTimestamp("hour").toLocalDateTime().toString(),
                        rs.getDouble("community_produced"),
                        rs.getDouble("community_used"),
                        rs.getDouble("grid_used"),
                        rs.getDouble("community_depleted"),
                        rs.getDouble("grid_portion")
                )
        );

        return result.stream().findFirst();
    }

    public List<HistoricalEnergyDto> findHistoricalEnergy(LocalDateTime start, LocalDateTime end) {
        String sql = """
                SELECT
                    hour,
                    community_produced,
                    community_used,
                    grid_used
                FROM energy_usage
                WHERE hour BETWEEN ? AND ?
                ORDER BY hour DESC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new HistoricalEnergyDto(
                        rs.getTimestamp("hour").toLocalDateTime().toString(),
                        rs.getDouble("community_produced"),
                        rs.getDouble("community_used"),
                        rs.getDouble("grid_used")
                ),
                start,
                end
        );
    }
}