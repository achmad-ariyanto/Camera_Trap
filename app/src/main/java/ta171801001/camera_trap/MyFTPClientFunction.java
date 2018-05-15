package ta171801001.camera_trap;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MyFTPClientFunction extends FTPClient {

    private static final String TAG = "Log";
    public FTPClient mFTPClient = null;

    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);
            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);
                /*
                 * Set File Transfer Mode
                 * To avoid corruption issue you must specified a correct
                 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
                 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
                 * transferring text, image, and compressed files.
                 */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error: could not connect to host " + host);
        }
        return false;
    }

    public boolean ftpDisconnect() {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    public String ftpGetCurrentWorkingDirectory()
    {
        try {
            String workingDir = mFTPClient.printWorkingDirectory();
            return workingDir;
        } catch(Exception e) {
            Log.d(TAG, "Error: could not get current working directory.");
        }

        return null;
    }

    public boolean ftpChangeDirectory(String directory_path)
    {
        try {
            mFTPClient.changeWorkingDirectory(directory_path);
        } catch(Exception e) {
            Log.d(TAG, "Error: could not change directory to " + directory_path);
        }

        return false;
    }

    public void ftpPrintFilesList(String dir_path, List<String> u, Context ctx)
    {
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;

            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    Log.i(TAG, "File : " + name);

                    String path = ftpGetCurrentWorkingDirectory()+"/"+name;
                    Log.i(TAG, "Path : " + path);
                    u.add(path);
                }
                else {
                    Log.i(TAG, "Directory : " + name);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean ftpMakeDirectory(String new_dir_path)
    {
        try {
            boolean status = mFTPClient.makeDirectory(new_dir_path);
            return status;
        } catch(Exception e) {
            Log.d(TAG, "Error: could not create new directory named " + new_dir_path);
        }

        return false;
    }

    public boolean ftpRemoveDirectory(String dir_path)
    {
        try {
            boolean status = mFTPClient.removeDirectory(dir_path);
            return status;
        } catch(Exception e) {
            Log.d(TAG, "Error: could not remove directory named " + dir_path);
        }

        return false;
    }

    public boolean ftpRemoveFile(String filePath)
    {
        try {
            boolean status = mFTPClient.deleteFile(filePath);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean ftpRenameFile(String from, String to)
    {
        try {
            boolean status = mFTPClient.rename(from, to);
            return status;
        } catch (Exception e) {
            Log.d(TAG, "Could not rename file: " + from + " to: " + to);
        }

        return false;
    }

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: path to the source file in FTP server
     * desFilePath: path to the destination file to be saved in sdcard
     */
    public boolean ftpDownload(String srcFilePath, Context ctx, String search)
    {
        boolean status = false;
        String[] bits = srcFilePath.split("/");
        String fileName= bits[bits.length-1];
        if(String.valueOf(srcFilePath).contains(search)){
            File appFolder = new File(String.valueOf(ctx.getExternalFilesDir(null)));
            if(!appFolder.exists()){
                appFolder.mkdir();
            }
            File dest = new File(appFolder, fileName);
            if (dest.exists()) dest.delete();
            try {
                dest.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Path", String.valueOf(dest));
            try {
                FileOutputStream desFileStream = new FileOutputStream(dest);
                status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
                desFileStream.close();
                Log.d("Status", "download Success");
                return status;
            } catch (Exception e) {
                Log.d(TAG, "download failed");
                Log.d(TAG, String.valueOf(e));
            }
        }

        return status;
    }

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: source file path in sdcard
     * desFileName: file name to be stored in FTP server
     * desDirectory: directory path where the file should be upload to
     */
    public boolean ftpUpload(String srcFilePath, String desFileName,
                             String desDirectory)
    {
        boolean status = false;
        try {
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);

            // change working directory to the destination directory
            if (ftpChangeDirectory(desDirectory)) {
                status = mFTPClient.storeFile(desFileName, srcFileStream);
            }

            srcFileStream.close();
            return status;
        } catch (Exception e) {
            Log.d(TAG, "upload failed");
        }

        return status;
    }

}
