package at.fhtw.disys.energy.controller;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import at.fhtw.disys.energy.service.EnergyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/current")
    public CurrentEnergyDto getCurrentEnergy() {
        return energyService.getCurrentEnergy();
    }

    @GetMapping("/historical")
    public List<HistoricalEnergyDto> getHistoricalEnergy(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        return energyService.getHistoricalEnergy(start, end);
    }
}