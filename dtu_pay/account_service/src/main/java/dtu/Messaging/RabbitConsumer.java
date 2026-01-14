package dtu.Messaging;

import org.jboss.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import dtu.Models.Database;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class RabbitConsumer {
    private static final Logger LOG = Logger.getLogger(RabbitConsumer.class);
    private RabbitMq rabbitmq;
    private Database db;

    private String EXCHANGE_NAME = "DTUPAY_EVENTS";
    private String PAYMENT_REQUEST_KEY = "payments.bankaccount.request";

    @PostConstruct
    void init() throws Exception {
        LOG.info("RabbitConsumer initialized: starting RabbitMQ consumer");
        
        this.rabbitmq = RabbitMq.getInstance();
        this.db = Database.getInstance();

        Channel channel = rabbitmq.getChannel();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, PAYMENT_REQUEST_KEY);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            LOG.info("RabbitConsumer received message: " + new String(delivery.getBody(), "UTF-8"));
            String accountId = new String(delivery.getBody(), "UTF-8");

            if (db.hasCustomer(accountId)) {
                String bankAccountId = db.getCustomer(accountId).bankAccountUuid();
                rabbitmq.publishBankAccountResponseEvent(bankAccountId);
            } else if (db.hasMerchant(accountId)) {
                String bankAccountId = db.getMerchant(accountId).bankAccountUuid();
                rabbitmq.publishBankAccountResponseEvent(bankAccountId);
            } else {
                rabbitmq.publishBankAccountResponseEvent(null);
            }
        };

        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
