package moodle.sync.fileserver;

import moodle.sync.util.FileServerFile;
import moodle.sync.util.UploadData.UploadElement;

import java.util.List;

public interface FileServerClient {

    List<FileServerFile> getFiles(String pathname) throws Exception;

    String uploadFile(UploadElement item, String pathname);

    void disconnect();

    void connect();
}
