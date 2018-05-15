/*
 * 
 * Copyright (C) 2010 Enterprise Distributed Technologies Ltd
 * 
 * www.enterprisedt.com
 */

import java.io.File;

import com.enterprisedt.net.ftp.FTPClientInterface;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.ssh.SSHFTPClient;
import com.enterprisedt.net.ftp.ssh.SSHFTPValidator;
import com.enterprisedt.net.ftp.ssh.SSHFTPPublicKey;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

public class UseSFTPWithServerValidationAuto {

    public static void main(String[] args) {

        // we want remote host, user name and password
        if (args.length < 4) {
            System.out
                    .println("Usage: run remote-host username password knownhostsfile");
            System.exit(1);
        }

        // extract command-line arguments
        String host = args[0];
        String username = args[1];
        String password = args[2];
        String knownHostsFile = args[3];
        String filename = "UseSFTPWithServerValidationAuto.java";

        // set up logger so that we get some output
        Logger log = Logger
                .getLogger(UseSFTPWithServerValidationAuto.class);
        Logger.setLevel(Level.INFO);

        try {
            // create client
            log.info("Creating SFTP client");
            SSHFTPClient ftp = new SSHFTPClient();

            // set remote host
            ftp.setRemoteHost(host);

            log.info("Setting user-name and password");
            ftp.setAuthentication(username, password);

            // best to have public keys for dsa and rsa in your
            // known hosts file
            log.info("Loading known hosts from " + knownHostsFile);
			ftp.setValidator(new UseSFTPWithServerValidationAuto.MyServerValidator());
            ftp.getValidator().loadKnownHosts(knownHostsFile);

            // connect to the server
            log.info("Connecting to server " + host);
            ftp.connect();
			
			// don't hash hostnames
			ftp.getValidator().setPortsInKnownHosts(true);

            log.info("Saving known_hosts");
            ftp.getValidator().saveKnownHosts();

            // Shut down client
            log.info("Quitting client");
            ftp.quit();

            log.info("Example complete");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
	/**
     * Class to add the server's public key to the known hosts memory store
     */
	static class MyServerValidator extends SSHFTPValidator {
	    private static Logger log = Logger.getLogger(MyServerValidator.class);
		
		protected boolean validate(String hostSpecifier, SSHFTPPublicKey publicKey, boolean hostKnown) {
			if (!hostKnown) {
			    log.info("Adding '" + hostSpecifier + "' to known hosts");
				try {
					addKnownHost(hostSpecifier, publicKey);
				}
				catch (Exception ex) {
					log.error("Failed to add host '" + hostSpecifier + "' to known hosts", ex);
				}
			}
			return true;
		}
	}

}
