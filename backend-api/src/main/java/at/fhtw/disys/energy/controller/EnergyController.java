package at.fhtw.disys.energy.controller;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import at.fhtw.disys.energy.service.EnergyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/energy")
@CrossOrigin
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
            @RequestParam String start,
            @RequestParam String end
    ) {
        return energyService.getHistoricalEnergy(start, end);
    }
}