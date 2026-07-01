package com.carcat.webhook.messaging;

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

    private final CarlandClientService carlandClientService;

    @RabbitListener(id = "${webhook.rabbit.listener-id}", queues = "${webhook.rabbit.queue}", autoStartup = "false")
    public void consume(QueuedVisitMessage message) {
        log.info("Carland yuxudan oyandi ona isteyi gonderdim");
        HttpMethod method = HttpMethod.valueOf(message.getHttpMethod());
        byte[] body = message.getBody().getBytes(StandardCharsets.UTF_8);
        carlandClientService.forwardFromQueue(method, message.getPath(), body);
    }
}
