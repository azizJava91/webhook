package com.carcat.webhook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestClient;

@SpringBootTest
class WebhookApplicationTests {

	@MockBean
	private RestClient restClient;

	@Test
	void contextLoads() {
	}

}
