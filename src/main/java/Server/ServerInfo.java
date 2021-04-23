package Server;

import Client.Pair;

import java.util.InvalidPropertiesFormatException;

public class ServerInfo {
     private String ipServer;
    private long portServer;
    private boolean isIP = false;
    private boolean IPServer = false;



    public ServerInfo(String ipServer, long portServer) throws InvalidPropertiesFormatException {
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
            throw new InvalidPropertiesFormatException(portServer + " is Invalid");
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


    public long getPortServer() {
        return portServer;
    }

    public String getIpAddressServer() {
        return ipServer;
    }

 }
