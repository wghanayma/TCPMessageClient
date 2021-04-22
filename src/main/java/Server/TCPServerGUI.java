package Server;

import javax.swing.*;

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
                buttonRunServer.setText("Stop Server");
                if (!textFieldIPServer.getText().isEmpty()) {
                    if (!textFieldPortServer.getText().isEmpty() && Integer.parseInt(textFieldPortServer.getText()) > 1024 && Integer.parseInt(textFieldPortServer.getText()) < 65536) {
                        int port = Integer.parseInt(textFieldPortServer.getText());
                        serverThread = new ServerThread(TCPServerGUI.this.listClient, port);
                        serverThread.start();
                        runServer = false;
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

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("TCP Server");
        jFrame.setContentPane(new TCPServerGUI().jPanelMain);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
    }
}
