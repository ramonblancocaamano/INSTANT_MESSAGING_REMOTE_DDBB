package webSocketService;

import apiREST.Cons;
import com.google.gson.Gson;
import entity.Message;
import entity.Topic;
import util.Subscription_close;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import subscriber.Subscriber;
import util.Subscription_request;

/**
 * @Author: BLANCO CAAMANO, Ramon <ramonblancocaamano@gmail.com>
 */
@ClientEndpoint
public class WebSocketClient {

    static Map<Topic, Subscriber> subscriberMap;
    static Session session;

    public static void newInstance() {
        subscriberMap = new HashMap<Topic, Subscriber>();
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(WebSocketClient.class,
                    URI.create(Cons.SERVER_WEBSOCKET));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try {
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void addSubscriber(Topic topic, Subscriber subscriber) {
        try {
            Subscription_request s_req;
            boolean hasTopic;

            hasTopic = subscriberMap.containsKey(topic);
            if (!hasTopic) {
                s_req = new Subscription_request(topic, Subscription_request.Type.ADD);
                session.getBasicRemote().sendText(new Gson().toJson(s_req));
                subscriberMap.put(topic, subscriber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void removeSubscriber(Topic topic) {
        try {
            Subscription_request s_req;
            boolean hasTopic;

            hasTopic = subscriberMap.containsKey(topic);
            if (hasTopic) {
                s_req = new Subscription_request(topic, Subscription_request.Type.REMOVE);
                session.getBasicRemote().sendText(new Gson().toJson(s_req));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String json) {

        Subscriber subscriber;
        Subscription_close s_close;
        Message message;

        s_close = new Gson().fromJson(json, Subscription_close.class);
        message = new Gson().fromJson(json, Message.class);

        if (s_close.cause == null) {
            subscriber = subscriberMap.get(message.topic);
            subscriber.onMessage(message);
        } else {
            subscriber = subscriberMap.get(s_close.topic);
            subscriber.onClose(s_close);
            subscriberMap.remove(s_close.topic);
        }
    }

}
