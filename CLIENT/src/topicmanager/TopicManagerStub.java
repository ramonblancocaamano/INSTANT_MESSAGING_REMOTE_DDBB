package topicmanager;

import apiREST.apiREST_Message;
import apiREST.apiREST_Publisher;
import apiREST.apiREST_Subscriber;
import apiREST.apiREST_Topic;
import entity.Message;
import util.Subscription_check;
import entity.Topic;
import util.Topic_check;
import entity.User;
import java.util.List;
import publisher.Publisher;
import publisher.PublisherStub;
import subscriber.Subscriber;
import webSocketService.WebSocketClient;

public class TopicManagerStub implements TopicManager {

    public User user;

    public TopicManagerStub(User user) {
        WebSocketClient.newInstance();
        this.user = user;
    }

    @Override
    public void close() {
        WebSocketClient.close();
    }

    @Override
    public Publisher addPublisherToTopic(Topic topic) {
        Publisher publisher;
        entity.Publisher entity;
        
        publisher = new PublisherStub(topic);
        entity = new entity.Publisher();
        entity.setUser(user);
        entity.setTopic(topic);
        
        apiREST_Publisher.createPublisher(entity);
        return publisher;
    }

    @Override
    public void removePublisherFromTopic(Topic topic) {
        entity.Publisher entity;
        Boolean hasTopic; 
        
        entity =  apiREST_Publisher.PublisherOf(user);
        hasTopic = entity.getTopic().equals(topic);
        
        if(hasTopic) {
            apiREST_Publisher.deletePublisher(entity);
        }
    }

    @Override
    public Topic_check isTopic(Topic topic) {
        Topic_check check;

        check = apiREST_Topic.isTopic(topic);
        return check;
    }

    @Override
    public List<Topic> topics() {
        List<Topic> topics;

        topics = apiREST_Topic.allTopics();
        return topics;
    }

    @Override
    public Subscription_check subscribe(Topic topic, Subscriber subscriber) {
        entity.Subscriber entity;
        Subscription_check check;
        boolean open;

        open = isTopic(topic).isOpen;
        if (!open) {
            check = new Subscription_check(topic, Subscription_check.Result.NO_TOPIC);
            return check;
        }
        entity = new entity.Subscriber();
        entity.setUser(user);
        entity.setTopic(topic);
        
        apiREST_Subscriber.createSubscriber(entity);
        WebSocketClient.addSubscriber(topic, subscriber);
        check = new Subscription_check(topic, Subscription_check.Result.OKAY);
        
        return check;   
    }

    @Override
    public Subscription_check unsubscribe(Topic topic, Subscriber subscriber) {
        entity.Subscriber entity;
        Subscription_check check;
        boolean open;

        open = isTopic(topic).isOpen;
        if (!open) {
            check = new Subscription_check(topic, Subscription_check.Result.NO_TOPIC);
            return check;
        }

        entity = new entity.Subscriber();
        entity.setUser(user);
        entity.setTopic(topic);
        
        apiREST_Subscriber.deleteSubscriber(entity);        
        WebSocketClient.removeSubscriber(topic);
        check = new Subscription_check(topic, Subscription_check.Result.NO_SUBSCRIPTION);
        
        return check;
    }

    @Override
    public Publisher publisherOf() {
        entity.Publisher entity;
        Publisher publisher;
        
        entity = apiREST_Publisher.PublisherOf(user);
        if (entity != null) {
            publisher = new PublisherStub(entity.getTopic());
            return publisher;
        }
        return null;
    }

    @Override
    public List<entity.Subscriber> mySubscriptions() {
        List<entity.Subscriber> subscribers;
        
        subscribers = apiREST_Subscriber.mySubscriptions(user);
        return subscribers;  
    }

    @Override
    public List<Message> messagesFrom(Topic topic) {
        List<Message> messages;
        
        messages = apiREST_Message.messagesFromTopic(topic);
        return messages;
    }

}
