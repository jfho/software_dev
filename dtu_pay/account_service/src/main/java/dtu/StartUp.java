package dtu;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.services.CustomerService;
import dtu.services.MerchantService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;

@Startup
public class StartUp {

	@PostConstruct
	void init() throws Exception {
		new CustomerService(new RabbitMqQueue("rabbitmq"));
		new MerchantService(new RabbitMqQueue("rabbitmq"));
	}
}
