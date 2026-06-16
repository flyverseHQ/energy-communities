package at.fhtw.disys.producer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public WeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public double getSolarWeatherFactor() {
        try {
            String response = restClient.get()
                    .uri("https://api.open-meteo.com/v1/forecast?latitude=48.2082&longitude=16.3738&current=cloud_cover,is_day")
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.path("current");

            int isDay = current.path("is_day").asInt(1);
            double cloudCover = current.path("cloud_cover").asDouble(50.0);

            if (isDay == 0) {
                return 0.0;
            }

            double cloudFactor = 1.0 - (cloudCover / 100.0);
            double factor = 0.3 + (cloudFactor * 0.7);

            return Math.max(0.3, Math.min(1.0, factor));
        } catch (Exception exception) {
            System.err.println("Weather API unavailable. Using fallback weather factor.");
            return 0.7;
        }
    }
}