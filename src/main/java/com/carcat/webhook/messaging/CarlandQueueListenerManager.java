package com.carcat.webhook.messaging;

import com.carcat.webhook.config.RabbitProperties;
import com.carcat.webhook.service.CarlandAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarlandQueueListenerManager {

    private final CarlandAvailabilityService carlandAvailabilityService;
    private final RabbitListenerEndpointRegistry listenerRegistry;
    private final RabbitProperties rabbitProperties;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        syncListenerState();
    }

    @Scheduled(fixedDelayString = "${webhook.rabbit.poll-interval-ms:30000}")
    public void syncListenerState() {
        log.info("Carlandin veziyyeti kontrol edilir");
        boolean available = carlandAvailabilityService.isAvailable();
        log.info("Carland veziyyeti: {}", available ? "islemir" : "isleyir");
        MessageListenerContainer container = listenerRegistry.getListenerContainer(rabbitProperties.getListenerId());

        if (container == null) {
            log.warn("Rabbit listener container not found: {}", rabbitProperties.getListenerId());
            return;
        }

        if (available) {
            if (!container.isRunning()) {
                container.start();
                log.info("Carland isleyir, Rabbit queue consumer basladi");
            }
            return;
        }

        if (container.isRunning()) {
            container.stop();
            log.info("Carland is down, Rabbit queue consumer paused (next check in {} ms)",
                    rabbitProperties.getPollIntervalMs());
        }
    }
}
