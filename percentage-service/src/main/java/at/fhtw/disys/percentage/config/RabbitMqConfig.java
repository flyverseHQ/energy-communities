package at.fhtw.disys.percentage.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMqConfig {

    @Value("${app.rabbitmq.queue.usage-updates}")
    private String usageUpdatesQueueName;

    @Bean
    public Queue usageUpdatesQueue() {
        return new Queue(usageUpdatesQueueName, true);
    }
}