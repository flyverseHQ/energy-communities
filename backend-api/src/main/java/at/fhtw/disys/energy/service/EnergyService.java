package at.fhtw.disys.energy.service;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyService {

    public CurrentEnergyDto getCurrentEnergy() {
        return new CurrentEnergyDto(78.54, 7.23);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(String start, String end) {
        return List.of(
                new HistoricalEnergyDto("2025-01-10T14:00:00", 143.024, 130.101, 14.75),
                new HistoricalEnergyDto("2025-01-10T13:00:00", 120.500, 110.300, 10.20)
        );
    }
}