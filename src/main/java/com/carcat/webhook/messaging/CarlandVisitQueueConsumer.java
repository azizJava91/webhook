package com.carcat.webhook.messaging;

import com.carcat.webhook.service.CarlandAvailabilityService;
import com.carcat.webhook.service.CarlandClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarlandVisitQueueConsumer {

    private final CarlandAvailabilityService carlandAvailabilityService;
    private final CarlandClientService carlandClientService;

    @RabbitListener(queues = "${webhook.rabbit.queue}")
    public void consume(QueuedVisitMessage message) {
        if (!carlandAvailabilityService.isAvailable()) {
            throw new IllegalStateException("Carland is still unavailable, message will be requeued");
        }

        log.info("Carland yuxudan oyandi ona isteyi gonderdim");
        HttpMethod method = HttpMethod.valueOf(message.getHttpMethod());
        byte[] body = message.getBody().getBytes(StandardCharsets.UTF_8);
        carlandClientService.forwardFromQueue(method, message.getPath(), body);
    }
}
