package at.fhtw.disys.producer.service;

import at.fhtw.disys.producer.dto.EnergyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EnergyProducerService implements ApplicationRunner {

    private final RabbitTemplate rabbitTemplate;
    private final WeatherService weatherService;
    private final String queueName;
    private final Random random = new Random();

    public EnergyProducerService(
            RabbitTemplate rabbitTemplate,
            WeatherService weatherService,
            @Value("${energy.queue.name}") String queueName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.weatherService = weatherService;
        this.queueName = queueName;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        while (true) {
            sendProductionMessage();

            int delayInMillis = 1000 + random.nextInt(4001);
            Thread.sleep(delayInMillis);
        }
    }

    private void sendProductionMessage() {
        double kwh = calculateProducedKwh();

        EnergyMessage message = new EnergyMessage(
                "PRODUCER",
                "COMMUNITY",
                kwh,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(queueName, message);

        System.out.printf(
                "Sent PRODUCER message: %.4f kWh at %s%n",
                message.getKwh(),
                message.getDatetime()
        );
    }

    double calculateProducedKwh() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        double daylightFactor = calculateDaylightFactor(hour);
        double weatherFactor = weatherService.getSolarWeatherFactor();

        double maxProductionPerMinute = 0.08;
        double produced = maxProductionPerMinute * daylightFactor * weatherFactor;

        return Math.round(produced * 10000.0) / 10000.0;
    }

    private double calculateDaylightFactor(int hour) {
        if (hour < 6 || hour > 20) {
            return 0.0;
        }

        if (hour >= 11 && hour <= 15) {
            return 1.0;
        }

        if (hour >= 8 && hour <= 17) {
            return 0.7;
        }

        return 0.3;
    }
}