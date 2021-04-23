package Server;

import javax.swing.*;
import java.util.InvalidPropertiesFormatException;

public class TCPServerGUI {
    private JTextField textFieldPortServer;
    private JTextField textFieldIPServer;
    private JButton buttonRunServer;
    private JPanel jPanelMain;
    private JList<String> listClient;
    ServerThread serverThread;
    boolean runServer = true;

    TCPServerGUI() {
        buttonRunServer.addActionListener(e -> {
            if (runServer) {
                if (!textFieldIPServer.getText().isEmpty()) {
                    if (!textFieldPortServer.getText().isEmpty()) {
                        String serverIP = null;
                        try {
                            serverIP = getServerDetail().getIpAddressServer();
                            int port = 0;

                                port = (int) getServerDetail().getPortServer();
                                serverThread = new ServerThread(TCPServerGUI.this.listClient, serverIP, port);
                                serverThread.start();
                                runServer = false;
                                buttonRunServer.setText("Stop Server");

                        } catch (InvalidPropertiesFormatException | NumberFormatException invalidPropertiesFormatException) {
                            JOptionPane.showMessageDialog(jPanelMain, "Please valid Input ", "Server", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(jPanelMain, "Please put Server Port", "Server Port", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(jPanelMain, "Please put IP Server ", "IP Server", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                buttonRunServer.setText("Run Server");
                serverThread.interrupt();
                runServer = true;
            }
        });
    }

    public ServerInfo getServerDetail() throws NumberFormatException, InvalidPropertiesFormatException {
        return new ServerInfo(this.textFieldIPServer.getText(), Integer.parseInt(this.textFieldPortServer.getText()));
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("TCP Server");
        jFrame.setContentPane(new TCPServerGUI().jPanelMain);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
    }
}
