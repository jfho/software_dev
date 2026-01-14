package dtu.Controller;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import dtu.Models.Transaction;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;

public class PaymentController {
    private static final PaymentController INSTANCE = new PaymentController();
    private String EXCHANGE_NAME = "DTUPAY_EVENTS";

    private PaymentController() {
    }

    public static PaymentController getInstance() {
        return INSTANCE;
    }

    BankService_Service service = new BankService_Service();
    BankService bank = service.getBankServicePort();

    public void registerTransaction(Transaction transaction) throws Exception {
        // 1. consumes the token and merchant id and amount done
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        // 2. passes the token to the token manager done
        String routingKey = "payments.customerid.request";
        String message = transaction.tokenId() + "," + transaction.merchantId() + ","
                + transaction.payment().toString();

        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));

        // 3. consumes the customer id from the token manager
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "token.customerid.response");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            String rk = delivery.getEnvelope().getRoutingKey();
        };
        channel.basicConsume(queueName, true, deliverCallback,
                consumerTag -> {
                });
        // 4. if not null, send the customer id and the merchant id to the account
        // service
        // 5. consumes the bank account if not null
        // 6. passes the bank account to the bank service along with the amount to
        // transfer
        // 7. send the transaction to the reporting service

        channel.close();
        connection.close();
    }
}