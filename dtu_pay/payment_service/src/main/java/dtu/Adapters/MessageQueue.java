package dtu.Adapters;

public interface MessageQueue {
	void produce(String message, String routingKey);
	String consume(String routingKey) throws Exception;
}
