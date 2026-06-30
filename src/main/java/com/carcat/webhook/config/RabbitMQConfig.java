package com.carcat.webhook.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String DLQ_SUFFIX = ".dlq";

    @Bean
    DirectExchange webhookExchange(WebhookProperties properties) {
        return new DirectExchange(properties.getRabbit().getExchange(), true, false);
    }

    @Bean
    Queue webhookQueue(WebhookProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getQueue())
                .deadLetterExchange(properties.getRabbit().getExchange() + ".dlx")
                .build();
    }

    @Bean
    Queue webhookDeadLetterQueue(WebhookProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getQueue() + DLQ_SUFFIX).build();
    }

    @Bean
    DirectExchange webhookDeadLetterExchange(WebhookProperties properties) {
        return new DirectExchange(properties.getRabbit().getExchange() + ".dlx", true, false);
    }

    @Bean
    Binding webhookBinding(Queue webhookQueue, DirectExchange webhookExchange, WebhookProperties properties) {
        return BindingBuilder.bind(webhookQueue)
                .to(webhookExchange)
                .with(properties.getRabbit().getRoutingKey());
    }

    @Bean
    Binding webhookDeadLetterBinding(
            Queue webhookDeadLetterQueue,
            DirectExchange webhookDeadLetterExchange,
            WebhookProperties properties) {
        return BindingBuilder.bind(webhookDeadLetterQueue)
                .to(webhookDeadLetterExchange)
                .with(properties.getRabbit().getRoutingKey());
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
