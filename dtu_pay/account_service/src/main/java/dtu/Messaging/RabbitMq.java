package dtu.Messaging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;

public class RabbitMq {
    private static RabbitMq INSTANCE;

    private final Connection connection;
    private final Channel channel;

    private String EXCHANGE_NAME = "DTUPAY_EVENTS";
    private String DELETE_USER_ROUTING_KEY = "accounts.customer.deleted";
    private String ACCOUNT_RESPONSE_ROUTING_KEY = "account.bankaccount.response";

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

    public static synchronized RabbitMq getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RabbitMq();
        }
        return INSTANCE;
    }

    public Channel getChannel() {
        return channel;
    }

    public void publishUserDeletedEvent(String customerId) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        byte[] payload = customerId == null ? new byte[0] : customerId.getBytes("UTF-8");
        channel.basicPublish(EXCHANGE_NAME, DELETE_USER_ROUTING_KEY, null, payload);
    }

    public void publishBankAccountResponseEvent(String bankAccountId) throws IOException {
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        byte[] payload = bankAccountId == null ? new byte[0] : bankAccountId.getBytes("UTF-8");
        channel.basicPublish(EXCHANGE_NAME, ACCOUNT_RESPONSE_ROUTING_KEY, null, payload);
    }
}