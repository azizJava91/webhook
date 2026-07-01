package com.carcat.webhook.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    DirectExchange webhookExchange(RabbitProperties properties) {
        return new DirectExchange(properties.getExchange(), true, false);
    }

    @Bean
    Queue webhookQueue(RabbitProperties properties) {
        return new Queue(properties.getQueue(), true);
    }

    @Bean
    Binding webhookBinding(Queue webhookQueue, DirectExchange webhookExchange, RabbitProperties properties) {
        return BindingBuilder.bind(webhookQueue)
                .to(webhookExchange)
                .with(properties.getRoutingKey());
    }

    @Bean
    MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
