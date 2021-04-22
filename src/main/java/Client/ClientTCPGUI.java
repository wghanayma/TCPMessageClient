package Client;

import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Vector;

public class ClientTCPGUI {
    static boolean login = false;
    private JList<String> listUsers;
    private JTextField textFieldUsername;
    private JButton buttonLogin;
    private JButton buttonLogout;
    private JTextArea textAreaSend;
    private JTextArea textAreaMessage;
    private JButton sendButton;
    private JTextField textFieldServerIP;
    private JTextField textFieldServerPort;
    private JTextField textFieldLocalIP;
    private JPanel jPanelMain;
    private JTextField textFieldLocalPort;
    private JTextField textFieldRemoteIP;
    private JTextField textFieldRemotePort;
    private List<Client> clientList;
    private List<MessagesClient> clientsListmessages;
    private ClientThread clientThread;
    private static String RemoteIPSelection;
    private static String RemotePortSelection;
    private static String UsernamePortSelection;
    private static int messageReceivedSize;
    StringBuilder[] messageReceived;

    /**
     * We need to make the thread
     * the tread need to have login
     * and logout.
     */
    ClientTCPGUI() {
        clientList = new Vector<>();
        clientsListmessages = new Vector<>();
        sendButton.setEnabled(false);
        MessageEvent messageEvent = new MessageEventImpl();
        buttonLogin.addActionListener(e -> {
            if (!login) {
                if (!textFieldServerIP.getText().isEmpty()) {
                    if (!textFieldServerPort.getText().isEmpty()) {
                        if (!textFieldLocalIP.getText().isEmpty()) {
                            if (!textFieldLocalPort.getText().isEmpty()) {
                                if (!textFieldUsername.getText().isEmpty()) {
                                    try {
                                        String username = getUserDetail().getUserName();
                                        String localIP = getUserDetail().getIpAddress();
                                        int localPort = (int) getUserDetail().getPort();
                                        String serverIP = getUserDetail().getIpAddressServer();
                                        int serverPort = (int) getUserDetail().getPortServer();
                                        //  System.out.println(textFieldUsername.getText());
                                        try {
                                            Socket connectionSocket = new Socket(serverIP, serverPort);
                                            DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
                                            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("Login", Boolean.TRUE);
                                            jsonObject.put("Username", username);
                                            jsonObject.put("IP", localIP);
                                            jsonObject.put("Port", localPort);
                                            outToServer.writeBytes(jsonObject + "\n");
                                            connectionSocket.close();
                                            Client client = new Client((String) textFieldLocalIP.getText(), (String) textFieldUsername.getText(), (long) Integer.parseInt(textFieldLocalPort.getText()));
                                            if (Client.clientExist(clientList, client) != -1) {
                                                clientList.remove(Client.clientExist(clientList, client));
                                            }
                                            ClientTCPGUI.this.listUsers.setListData(ClientTCPGUI.clientsToString(clientList));
                                            ClientTCPGUI.this.clientThread = new ClientThread(ClientTCPGUI.this.listUsers, ClientTCPGUI.this.textAreaMessage, localPort, client, messageEvent);
                                            ClientTCPGUI.this.clientThread.start();
                                            ClientTCPGUI.this.textFieldUsername.setEnabled(login);
                                            login = true;
                                        } catch (ConnectException exception) {
                                            JOptionPane.showMessageDialog(null
                                                    , "Cannot Reach the Server Recheck the server IP or check your Connection", "Client Check"
                                                    , JOptionPane.ERROR_MESSAGE);
                                        } catch (NumberFormatException numberFormatException) {
                                            JOptionPane.showMessageDialog(null
                                                    , "please enter a valid port number from (1024 - 65536)", "Client Check"
                                                    , JOptionPane.ERROR_MESSAGE);
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        }
                                    } catch (InvalidPropertiesFormatException invalidPropertiesFormatException) {
                                        JOptionPane.showMessageDialog(null
                                                , "please enter a valid number  ", "Client Check"
                                                , JOptionPane.ERROR_MESSAGE);
                                    } catch (NumberFormatException exception) {
                                        JOptionPane.showMessageDialog(null
                                                , "please enter a valid port number from (1024 - 65536)", "Client Check"
                                                , JOptionPane.ERROR_MESSAGE);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null
                                            , "please enter a valid username", "Client Check"
                                            , JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null
                                        , "please enter a valid Local port number from (1024 - 65536)", "Client Check"
                                        , JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null
                                    , "please enter a valid Local IP number ", "Client Check"
                                    , JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null
                                , "please enter a valid server port number from (1024 - 65536)", "Client Check"
                                , JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null
                            , "please enter a valid server IP number ", "Client Check"
                            , JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonLogout.addActionListener(e -> {
            if (login) {
                String username = null;
                try {
                    username = getUserDetail().getUserName();
                    String localIP = getUserDetail().getIpAddress();
                    int localPort = (int) getUserDetail().getPort();
                    String serverIP = getUserDetail().getIpAddressServer();
                    int serverPort = (int) getUserDetail().getPortServer();
                    try {
                        Socket connectionSocket = new Socket(serverIP, serverPort);
                        DataOutputStream outToServer = new DataOutputStream(connectionSocket.getOutputStream());
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Logout", Boolean.TRUE);
                        jsonObject.put("Username", username);
                        jsonObject.put("IP", localIP);
                        jsonObject.put("Port", localPort);
                        outToServer.writeBytes(jsonObject + "\n");
                        clientList.clear();
                        listUsers.setListData(ClientTCPGUI.clientsToString(clientList));
                        ClientTCPGUI.this.clientThread.interrupt();
                        connectionSocket.close();
                        ClientTCPGUI.this.textFieldUsername.setEnabled(login);
                        login = false;
                    } catch (ConnectException exception) {
                        JOptionPane.showMessageDialog(null
                                , "Cannot Reach the Server Recheck the server IP or check your Connection", "Server Check"
                                , JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                } catch (InvalidPropertiesFormatException invalidPropertiesFormatException) {
                    JOptionPane.showMessageDialog(null
                            , "please enter a valid IP number  ", "Client Check"
                            , JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(null
                            , "please enter a valid port number from (1024 - 65536)", "Client Check"
                            , JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        sendButton.addActionListener(e -> {
            System.out.println("sendButton" + UsernamePortSelection);
            if ((RemoteIPSelection != null) && (RemotePortSelection != null) && !(RemoteIPSelection.isEmpty()) && !(RemotePortSelection.isEmpty())) {
                new Thread(() -> {
                    {
                        try {
                            Socket clientSocket = new Socket(RemoteIPSelection, (int) Integer.parseInt(RemotePortSelection));
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Username", textFieldUsername.getText());
                            jsonObject.put("Message", textAreaSend.getText());
                            //  textAreaMessage.setText(messageReceived(MessagesClient.messagesClientUsernameExist(clientsListmessages, UsernamePortSelection), textFieldUsername.getText(), textAreaSend.getText()).toString());
                            System.out.println("sendButton UsernamePortSelection index : " + MessagesClient.messagesClientUsernameExist(clientsListmessages, UsernamePortSelection));
                            clientsListmessages.add(new MessagesClient(UsernamePortSelection, textFieldUsername.getText() + ": " + textAreaSend.getText() + "\n"));
                            textAreaMessage.setText("");
                            StringBuilder str = new StringBuilder();
                            textAreaMessage.setText("");
                            for (int i = 0; i < clientsListmessages.size(); i++) {
                                if (clientsListmessages.get(i).username.equals(UsernamePortSelection)) {
                                    str.append(clientsListmessages.get(i).message);
                                    System.out.println(i + " : " + clientsListmessages.get(i).message);
                                }
                            }
                            textAreaMessage.setText(str.toString());
                            //send to anther user
                            outToServer.writeBytes(jsonObject.toJSONString() + "\n");
                            clientSocket.close();
                            textAreaSend.setText(null);
                        } catch (UnknownHostException unknownHostException) {
                            unknownHostException.printStackTrace();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        for (MessagesClient clientsListmessage : clientsListmessages) {
            System.out.println("listUsers : : " + clientsListmessage.username + " - " + clientsListmessage.message);
        }
        listUsers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String dataUserName = listUsers.getSelectedValue().toString();
                    String[] arrOfStr = dataUserName.split(" - ");
                    // setUsernamePortSelection( arrOfStr[0]);
                    UsernamePortSelection = arrOfStr[0];
                    RemoteIPSelection = arrOfStr[1];
                    RemotePortSelection = arrOfStr[2];
                    textFieldRemoteIP.setText(arrOfStr[1]);
                    textFieldRemotePort.setText(arrOfStr[2]);
                    sendButton.setEnabled(login);
                    System.out.println("UsernamePortSelection listUsers" + UsernamePortSelection);
                    System.out.println("UsernamePortSelection listUsers clientsListmessages" + clientsListmessages.size());
                    // System.out.println("UsernamePortSelection listUsers " + MessagesClient.messagesClientUsernameExist(clientsListmessages, UsernamePortSelection));
                }
                textAreaMessage.setText("");
                if (UsernamePortSelection != null) {
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < clientsListmessages.size(); i++) {
                        textAreaMessage.setText("");
                        if (clientsListmessages.get(i).username.equals(UsernamePortSelection)) {
                            str.append(clientsListmessages.get(i).message);
                            System.out.println(i + " : " + clientsListmessages.get(i).message);
                        }
                    }
                    textAreaMessage.setText(str.toString());
                }
            }
        });
    }

    public static String[] clientsToString(List<Client> list) {
        String[] strings = new String[list.size()];
        int i = 0;
        for (Client c : list) {
            strings[i++] = c.toString();
        }
        return strings;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Client Chat");
        try {
            jFrame.setContentPane(new ClientTCPGUI().jPanelMain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setMinimumSize(new Dimension(850, 300));

        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
    }

    class MessageEventImpl implements MessageEvent {
        @Override
        public void onMessageReceived(List<MessagesClient> mClientsListmessages) {
            // for (int i = 0; i < mClientsListmessages.size(); i++) {
            System.out.println("mClientsListmessages : " + mClientsListmessages.size());
            clientsListmessages.add(new MessagesClient(mClientsListmessages.get(mClientsListmessages.size() - 1).username, mClientsListmessages.get(mClientsListmessages.size() - 1).username + ": " + mClientsListmessages.get(mClientsListmessages.size() - 1).message + "\n"));
            //  }
            StringBuilder str = new StringBuilder();
            textAreaMessage.setText("");
            if (UsernamePortSelection != null) {
                for (int i = 0; i < clientsListmessages.size(); i++) {
                    if (clientsListmessages.get(i).username.equals(UsernamePortSelection)) {
                        str.append(clientsListmessages.get(i).message);
                        System.out.println(i + " : " + clientsListmessages.get(i).message);
                    }
                }
                textAreaMessage.setText(str.toString());
            }
            // System.out.println("MessageEventImpl" + listUsers.getModel().getSize());
        }
    }

    public ClientInfo getUserDetail() throws NumberFormatException, InvalidPropertiesFormatException {
        return new ClientInfo(this.textFieldUsername.getText(), this.textFieldLocalIP.getText(), Integer.parseInt(this.textFieldLocalPort.getText()), this.textFieldServerIP.getText(), Integer.parseInt(this.textFieldServerPort.getText()));
    }
}
