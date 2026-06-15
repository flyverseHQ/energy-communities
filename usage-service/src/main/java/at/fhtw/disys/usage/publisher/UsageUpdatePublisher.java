package at.fhtw.disys.usage.publisher;

import at.fhtw.disys.usage.dto.UsageUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsageUpdatePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final String queueName;

    public UsageUpdatePublisher(
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Value("${usage.update.queue.name}") String queueName
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.queueName = queueName;
    }

    public void publish(UsageUpdateMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(queueName, json);

            System.out.printf(
                    "Published usage update for hour %s: produced=%.4f, communityUsed=%.4f, gridUsed=%.4f%n",
                    message.getHour(),
                    message.getCommunityProduced(),
                    message.getCommunityUsed(),
                    message.getGridUsed()
            );
        } catch (Exception exception) {
            System.out.printf("Could not publish usage update: %s%n", exception.getMessage());
        }
    }
}
