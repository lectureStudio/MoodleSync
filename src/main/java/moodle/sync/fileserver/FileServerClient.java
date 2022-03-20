package moodle.sync.fileserver;

import moodle.sync.util.FileServerFile;
import moodle.sync.util.UploadElement;

import java.util.List;

public interface FileServerClient {

    List<FileServerFile> getFiles(String pathname);

    boolean uploadFile(UploadElement item, String pathname);

    void disconnect();

    void connect();
}
