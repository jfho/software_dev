/**
 * @author s253874
 */

package dtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import dtu.messagingUtils.Event;
import dtu.messagingUtils.MessageQueue;

public class MockQueue implements MessageQueue {

    private final Map<String, List<Consumer<Event>>> handlers = new HashMap<>();

    @Override
    public void publish(Event event) {
        String topic = event.getType();
        
        List<Consumer<Event>> subscribers = handlers.get(topic);
        
        if (subscribers != null) {
            for (Consumer<Event> handler : subscribers) {
                handler.accept(event);
            }
        }
    }

    @Override
    public void addHandler(String topic, Consumer<Event> handler) {
        handlers.computeIfAbsent(topic, k -> new ArrayList<>()).add(handler);
    }
}