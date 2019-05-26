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
        entity.Publisher publisher;
        Boolean hasTopic; 
        
        publisher =  apiREST_Publisher.PublisherOf(user);
        hasTopic = publisher.topic().equals(topic);
        
        if(hasTopic) {
            apiREST_Publisher.deletePublisher(publisher);
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
        Subscription_check subs_check;
        Topic_check topic_check;

        topic_check = isTopic(topic);
        if (topic_check.isOpen == false) {
            subs_check = new Subscription_check(topic, Subscription_check.Result.NO_TOPIC);
            return subs_check;
        }

        WebSocketClient.addSubscriber(topic, subscriber);
        subs_check = new Subscription_check(topic, Subscription_check.Result.OKAY);
        return subs_check;
    }

    @Override
    public Subscription_check unsubscribe(Topic topic, Subscriber subscriber) {
        Subscription_check subs_check;
        Topic_check topic_check;

        topic_check = isTopic(topic);
        if (topic_check.isOpen == false) {
            subs_check = new Subscription_check(topic, Subscription_check.Result.NO_TOPIC);
            return subs_check;
        }

        WebSocketClient.removeSubscriber(topic);
        subs_check = new Subscription_check(topic, Subscription_check.Result.NO_SUBSCRIPTION);
        return subs_check;
    }

    @Override
    public Publisher publisherOf() {
        Publisher publisher;
        
        publisher = (Publisher) apiREST_Publisher.PublisherOf(user);
        return publisher;
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
