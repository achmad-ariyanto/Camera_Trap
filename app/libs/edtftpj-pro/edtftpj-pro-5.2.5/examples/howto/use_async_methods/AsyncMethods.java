/*
 * 
 * Copyright (C) 2006 Enterprise Distributed Technologies Ltd
 * 
 * www.enterprisedt.com
 */

import java.io.File;
import java.io.IOException;

import com.enterprisedt.net.ftp.AsyncFileTransferClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.Protocol;
import com.enterprisedt.net.ftp.WriteMode;
import com.enterprisedt.net.ftp.async.ConnectResult;
import com.enterprisedt.net.ftp.async.DeleteFileResult;
import com.enterprisedt.net.ftp.async.DisconnectResult;
import com.enterprisedt.net.ftp.async.DownloadFileResult;
import com.enterprisedt.net.ftp.async.UploadFileResult;
import com.enterprisedt.net.ftp.async.AsyncCallback.Connect;
import com.enterprisedt.net.ftp.async.AsyncCallback.DeleteFile;
import com.enterprisedt.net.ftp.async.AsyncCallback.Disconnect;
import com.enterprisedt.net.ftp.async.AsyncCallback.DownloadFile;
import com.enterprisedt.net.ftp.async.AsyncCallback.UploadFile;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;

public class AsyncMethods implements Connect, UploadFile, DownloadFile, DeleteFile, Disconnect {
    
    private static AsyncFileTransferClient client = new AsyncFileTransferClient();
    
    private static Logger log = Logger.getLogger("AsyncMethods");
    
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
        Logger.setLevel(Level.INFO);
        
        AsyncMethods async = new AsyncMethods();

        try {
             // set params
            client.setRemoteHost(host);
            client.setUserName(username);
            client.setPassword(password);
            client.setProtocol(Protocol.FTP); // FTP is the default
            
            client.connectAsync(async, null);
            
            // do whatever .... meanwhile async methods are chugging away
            
            // we won't exit because of the thread pool

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onConnect(ConnectResult result)
            throws FTPException, IOException {
        result.endAsync();
        
        log.info("Connected successfully");
        
        // upload this source file
        client.uploadFileAsync("AsyncMethods.java", "AsyncMethods.java", this, null);
    }

    public void onUploadFile(UploadFileResult result) throws IOException, FTPException {
        result.endAsync();
        
        log.info("Uploaded file " + result.getRemoteFileName() + " successfully");
        
        // download it again
        client.downloadFileAsync("AsyncMethods.java.copy", "AsyncMethods.java", WriteMode.OVERWRITE, this, null);
        
    }

    public void onDownloadFile(DownloadFileResult result) throws IOException, FTPException {
        result.endAsync();
        
        log.info("Downloaded file " + result.getRemoteFileName() + " successfully");
        
        // now delete local file
        File file = new File(result.getLocalFileName());
        file.delete();
        
        // and launch async delete of remote file
        client.deleteFileAsync(result.getRemoteFileName(), this, null);      
    }

    public void onDeleteFile(DeleteFileResult result) throws IOException, FTPException {
        result.endAsync();
        
        log.info("Deleted file " + result.getRemoteFileName() + " successfully");
        
        // now disconnect
        client.disconnectAsync(this, null);
    }

    public void onDisconnect(DisconnectResult result) throws FTPException, IOException {
        result.endAsync();
        
        log.info("Disconnected successfully");       
    }

}
