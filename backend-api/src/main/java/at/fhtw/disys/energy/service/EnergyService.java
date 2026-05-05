package at.fhtw.disys.energy.service;

import at.fhtw.disys.energy.dto.CurrentEnergyDto;
import at.fhtw.disys.energy.dto.HistoricalEnergyDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnergyService {

    public CurrentEnergyDto getCurrentEnergy() {
        // Hier könnte später die echte Datenbank-Abfrage stehen.

        return new CurrentEnergyDto(65.5, 34.5);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(String start, String end) {
        // Hier nutzen wir nun die vom Frontend übergebenen Parameter (start/end),
        // damit die API dynamisch auf die Kalender-Eingaben reagiert!
        return List.of(
                new HistoricalEnergyDto(start + "T08:00:00", 150.0, 140.0, 10.0),
                new HistoricalEnergyDto(start + "T12:00:00", 180.5, 170.0, 2.5),
                new HistoricalEnergyDto(end + "T18:00:00", 120.0, 110.0, 25.0)
        );
    }
}