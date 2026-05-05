package at.fhtw.disys.energy.service;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyService {

    private final JdbcTemplate jdbcTemplate;

    public EnergyService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CurrentEnergyDto getCurrentEnergy() {
        String sql = """
                SELECT community_depleted, grid_portion
                FROM energy_usage
                ORDER BY hour DESC
                LIMIT 1
                """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new CurrentEnergyDto(
                        rs.getDouble("community_depleted"),
                        rs.getDouble("grid_portion")
                )
        );
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(String start, String end) {
        String sql = """
                SELECT hour, community_produced, community_used, grid_used
                FROM energy_usage
                WHERE hour BETWEEN ?::timestamp AND ?::timestamp
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