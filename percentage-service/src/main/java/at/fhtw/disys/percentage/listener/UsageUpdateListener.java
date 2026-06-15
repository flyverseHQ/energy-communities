package at.fhtw.disys.percentage.listener;

import at.fhtw.disys.percentage.dto.UsageUpdateMessage;
import at.fhtw.disys.percentage.repository.PercentageRepository;
import at.fhtw.disys.percentage.service.PercentageCalculationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UsageUpdateListener {

    private final PercentageCalculationService calculationService;
    private final PercentageRepository percentageRepository;

    public UsageUpdateListener(
            PercentageCalculationService calculationService,
            PercentageRepository percentageRepository
    ) {
        this.calculationService = calculationService;
        this.percentageRepository = percentageRepository;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.usage-updates}")
    public void handleUsageUpdate(UsageUpdateMessage message) {
        LocalDateTime hour = LocalDateTime.parse(message.hour());

        double communityDepleted = calculationService.calculateCommunityDepleted(
                message.communityProduced(),
                message.communityUsed()
        );

        double gridPortion = calculationService.calculateGridPortion(
                message.communityUsed(),
                message.gridUsed()
        );

        percentageRepository.upsertCurrentPercentage(
                hour,
                communityDepleted,
                gridPortion
        );

        System.out.printf(
                "Updated current percentages for hour %s: communityDepleted=%.2f%%, gridPortion=%.2f%%%n",
                hour,
                communityDepleted,
                gridPortion
        );
    }
}