package moodle.sync.core.fileserver;

import java.util.List;

/**
 * Interface declaring all needed methods for fileserver support.
 *
 * @author Daniel Schröter
 */
public interface FileServerClient {

    List<FileServerFile> getFiles(String pathname) throws Exception;

    //    String uploadFile(syncTableElement item, String pathname);

    void disconnect();

    void connect();

}
