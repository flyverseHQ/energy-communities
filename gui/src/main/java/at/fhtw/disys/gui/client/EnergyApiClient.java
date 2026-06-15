package at.fhtw.disys.gui.client;

import at.fhtw.disys.gui.dto.CurrentEnergyDto;
import at.fhtw.disys.gui.dto.HistoricalEnergyDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class EnergyApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EnergyApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CurrentEnergyDto getCurrentEnergy() throws IOException, InterruptedException {
        String json = sendGetRequest(BASE_URL + "/energy/current");
        return objectMapper.readValue(json, CurrentEnergyDto.class);
    }

    public List<HistoricalEnergyDto> getHistoricalEnergy(LocalDate startDate, LocalDate endDate)
            throws IOException, InterruptedException {
        String start = startDate + "T00:00:00";
        String end = endDate + "T23:59:59";

        String url = BASE_URL + "/energy/historical?start=" + start + "&end=" + end;
        String json = sendGetRequest(url);

        return objectMapper.readValue(json, new TypeReference<>() {
        });
    }

    private String sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Backend returned HTTP status " + response.statusCode());
        }

        return response.body();
    }
}