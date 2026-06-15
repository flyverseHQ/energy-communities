package at.fhtw.disys.energy.service;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import at.fhtw.disys.energy.repository.EnergyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnergyService {

    private final EnergyRepository energyRepository;

    public EnergyService(EnergyRepository energyRepository) {
        this.energyRepository = energyRepository;
    }

    public CurrentEnergyDto getCurrentEnergy() {
        return energyRepository.findCurrentEnergy()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No current energy data available."
                ));
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must not be after end date."
            );
        }

        return energyRepository.findHistoricalEnergy(start, end);
    }
}