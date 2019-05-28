package main;

import apiREST.apiREST_User;
import entity.User;
import topicmanager.TopicManagerStub;
import util.Login_check;

/**
 * @Author: BLANCO CAAMANO, Ramon <ramonblancocaamano@gmail.com>
 * @About: Execute one client at a time to open separate windows on Netbeans,
 * also because otherwise all clients share the same WebSocket connection.
 */
public class Client_Elisardo {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                Login_check login = new Login_check();
                login.login = "Elisardo";
                login.password = "1234";
                User user_john = apiREST_User.loginUser(login);

                SwingClient client = new SwingClient(new TopicManagerStub(user_john));
                client.createAndShowGUI();
            }
        });

    }
}
