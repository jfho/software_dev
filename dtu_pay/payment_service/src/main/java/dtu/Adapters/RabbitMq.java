package dtu.Adapters;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RabbitMq implements MessageQueue {
    private static final RabbitMq INSTANCE = new RabbitMq();

    private final Connection connection;
    private final Channel channel;

    private String EXCHANGE_NAME = "DTUPAY_EVENTS";

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

    public void produce(String message, String routingKey) {
        try {
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String consume(String routingKey) throws Exception {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);

        BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String rk = delivery.getEnvelope().getRoutingKey();
            response.offer(message);
        };

        channel.basicConsume(queueName, true, deliverCallback,
                consumerTag -> {
                });

        return response.take();
    }
}