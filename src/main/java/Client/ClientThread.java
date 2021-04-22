package Client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ClientThread extends Thread {
    private final JList<String> jList;
    private final List<Client> clientList;
    private final List<MessagesClient> clientsListmessages;
    private boolean run;
    private final Object sync = new Object();
    private final int port;
    private final Client clientCurrent;
    private final JTextArea textAreaMessage;
    private String UsernamePortSelection;
    private MessageEvent messageEvent;

    public ClientThread(JList<String> jList, JTextArea textAreaMessage, int port, Client clientCurrent, MessageEvent messageEvent) {
        this.textAreaMessage = textAreaMessage;
        run = true;
        this.port = port;
        this.jList = jList;
        this.clientCurrent = clientCurrent;
        this.messageEvent = messageEvent;
        clientList = new Vector<>();
        clientsListmessages = new Vector<>();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.run = false;
    }

    @Override
    public void run() {
        synchronized (sync) {
            ServerSocket socket = null;
            while (run) {
                try {
                    socket = new ServerSocket(this.port);
                    Socket connectionSocket = socket.accept();
                    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String sentence = inFromServer.readLine();
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(sentence);
                    if (jsonObject.containsKey("Message")) {
                        //  System.out.println("Message :::: " + jsonObject);
                        String usernameJson = (String) jsonObject.get("Username");
                        String messageJson = (String) jsonObject.get("Message");
                        MessagesClient messagesClient = new MessagesClient(usernameJson, messageJson);
                        //MessagesClient.messagesClientExist(clientsListmessages,messagesClient);
                       /* if (MessagesClient.messagesClientExist(clientsListmessages, messagesClient) != -1) {
                            StringBuilder str = new StringBuilder(clientsListmessages.get(MessagesClient.messagesClientExist(clientsListmessages, messagesClient)).message+ usernameJson + ": " + messageJson );
                            clientsListmessages.get(MessagesClient.messagesClientExist(clientsListmessages, messagesClient)).message = String.valueOf(str);

                        } else {*/
                        clientsListmessages.add(messagesClient);
                        // }
                        // textAreaMessage.setText(jList.getSelectedValue());
                        messageEvent.onMessageReceived(clientsListmessages);
                        System.out.println(clientsListmessages.size());
                    } else {
                        clientList.clear();
                        JSONArray clientsJSONArrayNewUser = (JSONArray) jsonObject.get("Array");
                        if (clientsJSONArrayNewUser != null) {
                            for (Object o : clientsJSONArrayNewUser) {
                                if (o != null) {
                                    JSONObject clientJSONObject = (JSONObject) o;
                                    Client client = new Client((String) clientJSONObject.get("IP"), (String) clientJSONObject.get("Username"), (Long) clientJSONObject.get("Port"));
                                    if (Client.clientExist(clientList, client) == -1) {
                                        clientList.add(client);
                                    }
                                    if (Client.clientExist(clientList, clientCurrent) != -1) {
                                        clientList.remove(Client.clientExist(clientList, clientCurrent));
                                    }
                                    jList.setListData(ClientTCPGUI.clientsToString(clientList));
                                }
                            }
                        }
                    }
                    connectionSocket.close();
                    socket.close();
                } catch (IOException e) {
                    if (socket != null && !socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException es) {
                            es.printStackTrace(System.err);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
