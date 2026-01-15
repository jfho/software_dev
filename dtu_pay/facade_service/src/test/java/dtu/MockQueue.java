package dtu;

import dtu.Adapters.MessageQueue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MockQueue implements MessageQueue {
    private Map<String, Queue<String>> queues = new HashMap<>();

    public void produce(String message, String routingKey) {
        queues.computeIfAbsent(routingKey, k -> new LinkedList<>()).add(message);
    }

    public String consume(String routingKey) throws Exception {
        Queue<String> queue = queues.get(routingKey);
        return queue.poll();
    }
}
