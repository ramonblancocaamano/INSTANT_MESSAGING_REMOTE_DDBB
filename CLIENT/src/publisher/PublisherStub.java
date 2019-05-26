package publisher;

import apiREST.apiREST_Message;
import entity.Message;
import entity.Topic;

/**
 * @Author: BLANCO CAAMANO, Ramon <ramonblancocaamano@gmail.com>
 */
public class PublisherStub implements Publisher {

    Topic topic;

    public PublisherStub(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void publish(Message message) {
        apiREST_Message.createMessage(message);
    }

    @Override
    public Topic topic() {
        return topic;
    }

}
