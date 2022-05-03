package moodle.sync.fileserver;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.util.UploadData.UploadElement;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the FileServerClient-interface using the ftp-protocol.
 *
 * @author Daniel Schröter
 */
public class FileServerClientFTP implements FileServerClient {

    //Used FTPClient for communication.
    private final FTPClient ftpClient;

    //Configuration providing information about url etc.
    private final MoodleSyncConfiguration config;


    public FileServerClientFTP(MoodleSyncConfiguration config) {
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        this.config = config;
    }

    /**
     * Establishes the connection with a fileserver.
     */
    @Override
    public void connect() {
        try {
            ftpClient.connect(config.getFileserver(), Integer.parseInt(config.getPortFileserver()));
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("Exception in connecting to FTP Server");
            }

            ftpClient.login(config.getUserFileserver(), config.getPasswordFileserver());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Terminates the connection with a fileserver.
     */
    @Override
    public void disconnect() {
        try {
            ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to gather information about uploaded files.
     *
     * @param pathname Path to the directory at the ftpserver.
     * @return a list containing elements of FileServerFile.
     */
    @Override
    public List<FileServerFile> getFiles(String pathname) throws Exception {
        List<FileServerFile> files = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(pathname);
            for (FTPFile item : ftpFiles) {
                files.add(new FileServerFile(item.getName(), item.getTimestamp().getTimeInMillis()));
            }
        } catch (Exception e) {
            throw new Exception();
        }
        return files;
    }

    /**
     * Method to upload a file to a ftpserver.
     *
     * @param item     UploadElement, containing the local path to the file.
     * @param pathname Dedicated directory at the ftpserver.
     * @return the url of the uploaded file.
     */
    @Override
    public String uploadFile(UploadElement item, String pathname) {
        //Evtl noch pathname einbringen
        String url = null;
        try {
            InputStream file = Files.newInputStream(item.getPath());
            ftpClient.storeFile("/"  /*+ config.getRecentSection().getName() + "/" */ + item.getPath().getFileName().toString(), file);
            //ToDo add functionality Url
            url = config.getFileserver() + "/" /*+  config.getRecentSection().getName() + "/" */ + item.getPath().getFileName().toString();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
