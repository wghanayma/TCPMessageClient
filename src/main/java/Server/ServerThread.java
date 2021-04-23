package Server;

import Client.Client;
import Client.ClientTCPGUI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class ServerThread extends Thread {
    private JList<String> jList;
    private List<Client> clientArrayList;
    private final Object sync = new Object();
    private int port;
    private boolean run;
    private String serverIP;
    private InetAddress inetAddress;
    ServerThread(JList<String> jList, String serverIP, int port) {
        this.jList = jList;
        clientArrayList = new Vector<>();
        run = true;
        this.port = port;
        this.serverIP = serverIP;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.run = false;
        clientArrayList.clear();
        jList.setListData(ClientTCPGUI.clientsToString(clientArrayList));
    }

    @Override
    public void run() {
        synchronized (sync) {
            ServerSocket socket = null;
            while (run) {
                try {
                    socket = new ServerSocket(port);
                    Socket connectionSocket = socket.accept();
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    String sentence = inFromClient.readLine();
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(sentence);
                    String username = (String) jsonObject.get("Username");
                    String ip = (String) jsonObject.get("IP");
                    long port = (long) jsonObject.get("Port");
                    Client client = new Client(ip, username, (int) port);
                    if (jsonObject.containsKey("Login") && run) {
                        //Login
                        if (Client.clientExist(clientArrayList, client) == -1) {
                            clientArrayList.add(client);
                            //   JSONArray jsonArray = new JSONArray();
          /*    for(Client c : clientArrayList){
                JSONObject clientJSON = new JSONObject();
                clientJSON.put("Username", c.getUsername());
                clientJSON.put("IP", c.getIp());
                clientJSON.put("Port", c.getPort());
                jsonArray.add(clientJSON);
              }*/
                            //    outToClient.writeBytes(jsonArray + "\n");
                            //  connectionSocket.close();
                            connectionSocket.close();
                            socket.close();
                            //the next code will send the info of the new user to the other clients.
                            for (Client c : clientArrayList) {
                                Socket sendSocket = new Socket(c.getIp(), (int) c.getPort());
                                DataOutputStream outToServer = new DataOutputStream(sendSocket.getOutputStream());
                                JSONArray jsArray = new JSONArray();
                                JSONObject clientObject = new JSONObject();
                                for (Client c1 : clientArrayList) {
                                    JSONObject clientJSON = new JSONObject();
                                    clientJSON.put("Username", c1.getUsername());
                                    clientJSON.put("IP", c1.getIp());
                                    clientJSON.put("Port", c1.getPort());
                                    jsArray.add(clientJSON);
                                }
                                clientObject.put("Array", jsArray);
                                outToServer.writeBytes(clientObject + "\n");
                                System.out.println("clientObject" + clientObject);
                                sendSocket.close();
                            }
                            jList.setListData(ClientTCPGUI.clientsToString(clientArrayList));
                        }
                    } else if (jsonObject.containsKey("Logout") && run) {
                        //Logout
                        System.out.println("Logout");
                        System.out.println(clientArrayList);
                        if (Client.clientExist(clientArrayList, client) != -1) {
                            List<Client> clientArrayListWithDel = clientArrayList;
                            clientArrayListWithDel.remove(Client.clientExist(clientArrayList, client));
                            for (Client c : clientArrayList) {
                                Socket sendSocket = new Socket(c.getIp(), (int) c.getPort());
                                DataOutputStream outToServer = new DataOutputStream(sendSocket.getOutputStream());
                                JSONArray jsArray = new JSONArray();
                                JSONObject clientObject = new JSONObject();
                                //  clientArrayList.remove(Client.clientExist(clientArrayList,client));
                                if (!clientArrayListWithDel.isEmpty()) {
                                    for (Client c1 : clientArrayListWithDel) {
                                        JSONObject clientJSON = new JSONObject();
                                        clientJSON.put("Username", c1.getUsername());
                                        clientJSON.put("IP", c1.getIp());
                                        clientJSON.put("Port", c1.getPort());
                                        jsArray.add(clientJSON);
                                    }
                                    clientObject.put("Array", jsArray);
                                    outToServer.writeBytes(clientObject + "\n");
                                }
                                System.out.println("clientObject" + clientObject);
                                sendSocket.close();
                            }
                            clientArrayList = clientArrayListWithDel;
                            jList.setListData(ClientTCPGUI.clientsToString(clientArrayList));
                            connectionSocket.close();
                            socket.close();
                        }
                    }
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
