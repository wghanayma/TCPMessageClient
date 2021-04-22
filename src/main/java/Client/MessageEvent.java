package Client;

import java.util.List;

public interface MessageEvent {
    void onMessageReceived(List<MessagesClient> mClientsListmessages);

}
