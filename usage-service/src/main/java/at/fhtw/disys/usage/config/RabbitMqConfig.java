package at.fhtw.disys.usage.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue energyMessagesQueue(@Value("${energy.queue.name}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue usageUpdatesQueue(@Value("${usage.update.queue.name}") String queueName) {
        return new Queue(queueName, true);
    }
}
