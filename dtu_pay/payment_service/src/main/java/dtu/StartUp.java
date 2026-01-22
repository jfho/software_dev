/**
 * @author s243019
 */

package dtu;

import dtu.adapters.BankClient;
import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.services.PaymentService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;

@Startup
public class StartUp {

	@PostConstruct
	void init() throws Exception {
		new PaymentService(new RabbitMqQueue("rabbitmq"), new BankClient());
	}
}
