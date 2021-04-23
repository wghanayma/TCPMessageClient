package Client;

import java.util.InvalidPropertiesFormatException;

public class ClientInfo {
    private String userName;
    private String ipAddress;
    private long port;
    private String ipServer;
    private long portServer;
    private boolean isIP = false;
    private boolean IPServer = false;

    public ClientInfo(String userName, String localIP, long localPort, String ipServer, long portServer) throws InvalidPropertiesFormatException {
        Pair<Boolean, String> isIpValid = validIPOrHostname(localIP);
        Pair<Boolean, String> isIpServerValid = validIPOrHostname(ipServer);

        if (isIpValid.getKey()) {
            if (isIpValid.getValue().equals("IP")) {
                this.isIP = true;
            }
            this.ipAddress = localIP;
        } else {
            throw new InvalidPropertiesFormatException(ipAddress + " is Invalid");
        }
        if (isIpServerValid.getKey()) {
            if (isIpServerValid.getValue().equals("IP")) {
                this.IPServer = true;
            }
            this.ipServer = ipServer;
        } else {
            throw new InvalidPropertiesFormatException(ipServer + " is Invalid");
        }
        if (validPort(port)) {
            this.port = localPort;
        } else {
            throw new InvalidPropertiesFormatException(port + " is Invalid");
        }
        if (validPort(portServer)) {
            this.portServer = portServer;
        } else {
            throw new InvalidPropertiesFormatException(port + " is Invalid");
        }
        this.userName = userName;
    }

    public ClientInfo(  String ipServer, long portServer) throws InvalidPropertiesFormatException {
         Pair<Boolean, String> isIpServerValid = validIPOrHostname(ipServer);


        if (isIpServerValid.getKey()) {
            if (isIpServerValid.getValue().equals("IP")) {
                this.IPServer = true;
            }
            this.ipServer = ipServer;
        } else {
            throw new InvalidPropertiesFormatException(ipServer + " is Invalid");
        }

        if (validPort(portServer)) {
            this.portServer = portServer;
        } else {
            throw new InvalidPropertiesFormatException(port + " is Invalid");
        }
     }
    public Pair<Boolean, String> validIPOrHostname(String ip) {
        String validIPAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        if (ip.matches(validIPAddressRegex)) {
            return new Pair<>(true, "IP");
        }
        return new Pair<>(false, "");
    }

    public boolean validPort(long port) {
        return (port > 1024 && port <= Math.pow(2, 16));
    }

    public long getPort() {
        return port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public long getPortServer() {
        return portServer;
    }

    public String getIpAddressServer() {
        return ipServer;
    }

    public String getUserName() {
        return userName;
    }
}
