package moodle.sync.fileserver;

import moodle.sync.config.MoodleSyncConfiguration;
import moodle.sync.util.FileServerFile;
import moodle.sync.util.UploadElement;
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

public class FileServerClientFTP implements FileServerClient{

    private FTPClient ftpClient;

    private final MoodleSyncConfiguration config;


    public FileServerClientFTP(MoodleSyncConfiguration config){
        ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        this.config = config;
    }

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
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try{
            ftpClient.disconnect();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<FileServerFile> getFiles(String pathname) {
        List<FileServerFile> files = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles(pathname);
            for(FTPFile item : ftpFiles){
                files.add(new FileServerFile(item.getName(), item.getTimestamp().getTimeInMillis()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return files;
    }

    @Override
    public String uploadFile(UploadElement item, String pathname) {
        //Evtl noch pathname einbringen
        String url = null;
        try{
            InputStream file  = Files.newInputStream(item.getPath());
            ftpClient.storeFile("/" + item.getPath().getFileName().toString() , file);
            //ToDo add functionality URL
            url = config.getFileserver() + "/" + pathname + "/" + item.getPath().getFileName().toString();
            file.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }

}
