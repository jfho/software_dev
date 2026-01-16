package dtu;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.services.TokenService;
import io.quarkus.runtime.Startup;

@Startup
public class StartUp {
	public StartUp() throws Exception {
		startUp();
	}

	private void startUp() throws Exception {
		new TokenService(new RabbitMqQueue("rabbitmq"));
	}
}
