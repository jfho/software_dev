/**
 * @author s214881
 */
package dtu;

import dtu.messagingUtils.implementations.RabbitMqQueue;
import dtu.services.TokenService;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;

@Startup
public class StartUp {

	@PostConstruct
	void init() throws Exception {
		new TokenService(new RabbitMqQueue("rabbitmq"));
	}
}
