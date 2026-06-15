package at.fhtw.disys.usage.listener;

import at.fhtw.disys.usage.dto.EnergyMessage;
import at.fhtw.disys.usage.service.UsageCalculationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class EnergyMessageListener {

    private final UsageCalculationService usageCalculationService;
    private final ObjectMapper objectMapper;

    public EnergyMessageListener(
            UsageCalculationService usageCalculationService,
            ObjectMapper objectMapper
    ) {
        this.usageCalculationService = usageCalculationService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${energy.queue.name}")
    public void receive(byte[] body) {
        try {
            String json = new String(body, StandardCharsets.UTF_8);
            EnergyMessage message = objectMapper.readValue(json, EnergyMessage.class);

            usageCalculationService.process(message);
        } catch (Exception exception) {
            System.out.printf("Could not process energy message: %s%n", exception.getMessage());
        }
    }
}
