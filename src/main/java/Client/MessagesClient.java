package Client;

import java.util.List;

public class MessagesClient {
    String username;
    String message;

    public MessagesClient(String username, String message) {
        this.message = message;
        this.username = username;
    }
    @Override
    public String toString() {
        return  message  ;
    }
    public boolean equal(MessagesClient messagesClient) {
        return this.username.equals(messagesClient.username);
    }

    public static int messagesClientExist(List<MessagesClient> list, MessagesClient messagesClient) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equal(messagesClient)) {
                return i;
            }
        }
        return -1;
    }
    public static int messagesClientUsernameExist(List<MessagesClient> list, String username) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equalUserName(username)) {
                System.out.println(list.get(i).username);
                System.out.println(username);

                return i;
            }
        }
        return -1;
    }

    private boolean equalUserName(String username) {
        if (this.username.equals(username)) return true;
        else return false;
    }
}
