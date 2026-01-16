package dtu;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.ReportService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;

@Startup
public class StartUp {

	@PostConstruct
	void init() throws Exception {
		new ReportService(new RabbitMqQueue("rabbitmq"));
	}
}
