package at.fhtw.disys.user.service;

import at.fhtw.disys.user.dto.EnergyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyUserService implements ApplicationRunner {

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;
    private final Random random = new Random();

    public EnergyUserService(
            RabbitTemplate rabbitTemplate,
            @Value("${energy.queue.name}") String queueName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            sendUsageMessage();

            int delayInMillis = 1000 + random.nextInt(4001);
            Thread.sleep(delayInMillis);
        }
    }

    private void sendUsageMessage() {
        double kwh = calculateUsedKwh();
        String timeOfDayPeriod = determineTimeOfDayPeriod(LocalDateTime.now().getHour());

        EnergyMessage message = new EnergyMessage(
                "USER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(queueName, message);

        System.out.printf(
                "Sent USER message: %.4f kWh at %s (%s consumption period)%n",
                message.getKwh(),
                message.getDatetime(),
                timeOfDayPeriod
        );
    }

    double calculateUsedKwh() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        double baseConsumption = 0.03;
        double timeOfDayFactor = calculateTimeOfDayFactor(hour);
        double randomFactor = 0.7 + random.nextDouble() * 0.6;

        double used = baseConsumption * timeOfDayFactor * randomFactor;

        return Math.round(used * 10000.0) / 10000.0;
    }

    private double calculateTimeOfDayFactor(int hour) {
        if (hour >= 6 && hour <= 9) {
            return 1.6;
        }

        if (hour >= 17 && hour <= 22) {
            return 1.8;
        }

        if (hour >= 0 && hour <= 5) {
            return 0.5;
        }

        return 1.0;
    }

    private String determineTimeOfDayPeriod(int hour) {
        if (hour >= 6 && hour <= 9) {
            return "morning peak";
        }

        if (hour >= 17 && hour <= 22) {
            return "evening peak";
        }

        if (hour >= 0 && hour <= 5) {
            return "night low";
        }

        return "normal";
    }
}
