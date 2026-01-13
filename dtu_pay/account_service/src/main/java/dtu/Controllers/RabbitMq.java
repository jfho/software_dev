package dtu.Controllers;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;

public class RabbitMq {
    private static final RabbitMq INSTANCE = new RabbitMq();

    private final Connection connection;
    private final Channel channel;

    private String EXCHANGE_NAME = "DTUPAY_EVENTS";
    private String DELETE_USER_ROUTING_KEY = "accounts.customer.deleted";

    private RabbitMq() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("rabbitmq");

            this.connection = factory.newConnection();
            this.channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RabbitMQ", e);
        }
    }

    public static RabbitMq getInstance() {
        return INSTANCE;
    }

    public void publishUserDeletedEvent(String customerId) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.basicPublish(EXCHANGE_NAME, DELETE_USER_ROUTING_KEY, null, customerId.getBytes("UTF-8"));
    }
}