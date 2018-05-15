/*
 * 
 * Copyright (C) 2006 Enterprise Distributed Technologies Ltd
 * 
 * www.enterprisedt.com
 */

import com.enterprisedt.net.ftp.SecureFileTransferClient;
import com.enterprisedt.net.ftp.Protocol;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

public class MultiProtocolClientFTPS {

    public static void main(String[] args) {

        // we want remote host, user name and password
        if (args.length < 3) {
            System.out
                    .println("Usage: run remote-host username password");
            System.exit(1);
        }

        // extract command-line arguments
        String host = args[0];
        String username = args[1];
        String password = args[2];

        // set up logger so that we get some output
        Logger log = Logger.getLogger("MultiProtocolClientFTPS");
        Logger.setLevel(Level.INFO);

        try {
            // create client
            log.info("Creating client");
            SecureFileTransferClient client = new SecureFileTransferClient();

            // set params
            client.setRemoteHost(host);
            client.setUserName(username);
            client.setPassword(password);
            client.setProtocol(Protocol.FTPS_EXPLICIT);
            
            client.connect();
            log.info("Connected to server " + host);
            
            // list the current directory
            FTPFile[] files = client.directoryList();
            for (int i = 0; i < files.length; i++) {
              log.info(files[i].toString());
            }

            // disconnect from server
            client.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
