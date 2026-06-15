package at.fhtw.disys.usage.service;

import at.fhtw.disys.usage.dto.EnergyMessage;
import at.fhtw.disys.usage.dto.UsageUpdateMessage;
import at.fhtw.disys.usage.publisher.UsageUpdatePublisher;
import at.fhtw.disys.usage.repository.UsageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UsageCalculationService {

    private final UsageRepository usageRepository;
    private final UsageUpdatePublisher usageUpdatePublisher;

    public UsageCalculationService(
            UsageRepository usageRepository,
            UsageUpdatePublisher usageUpdatePublisher
    ) {
        this.usageRepository = usageRepository;
        this.usageUpdatePublisher = usageUpdatePublisher;
    }

    @Transactional
    public void process(EnergyMessage message) {
        LocalDateTime hour = truncateToHour(message.getDatetime());

        if ("PRODUCER".equalsIgnoreCase(message.getType())) {
            processProducerMessage(hour, message.getKwh());
            return;
        }

        if ("USER".equalsIgnoreCase(message.getType())) {
            processUserMessage(hour, message.getKwh());
            return;
        }

        System.out.printf("Ignored unknown message type: %s%n", message.getType());
    }

    private void processProducerMessage(LocalDateTime hour, double kwh) {
        usageRepository.addProducedEnergy(hour, kwh);
        UsageUpdateMessage updateMessage = usageRepository.findByHour(hour);
        usageUpdatePublisher.publish(updateMessage);

        System.out.printf("Processed PRODUCER message: %.4f kWh for hour %s%n", kwh, hour);
    }

    private void processUserMessage(LocalDateTime hour, double kwh) {
        UsageUpdateMessage updateMessage = usageRepository.addUsedEnergy(hour, kwh);
        usageUpdatePublisher.publish(updateMessage);

        System.out.printf("Processed USER message: %.4f kWh for hour %s%n", kwh, hour);
    }

    private LocalDateTime truncateToHour(LocalDateTime datetime) {
        return datetime.withMinute(0).withSecond(0).withNano(0);
    }
}
