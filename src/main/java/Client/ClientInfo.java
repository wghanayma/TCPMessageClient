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

    public ClientInfo(String userName, String ipOrHostName, long port, String ipServer, long portServer) throws InvalidPropertiesFormatException {
        Pair<Boolean, String> isIpValid = validIPOrHostname(ipOrHostName);
        if (isIpValid.getKey()) {
            if (isIpValid.getValue().equals("IP")) {
                this.isIP = true;
            }
            this.ipAddress = ipOrHostName;
        } else {
            throw new InvalidPropertiesFormatException(ipOrHostName + " is Invalid");
        }
        if (isIpValid.getKey()) {
            if (isIpValid.getValue().equals("IP")) {
                this.IPServer = true;
            }
            this.ipServer = ipServer;
        } else {
            throw new InvalidPropertiesFormatException(ipOrHostName + " is Invalid");
        }
        if (validPort(port)) {
            this.port = port;
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
