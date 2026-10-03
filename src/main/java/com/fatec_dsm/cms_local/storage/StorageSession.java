package com.fatec_dsm.cms_local.storage;

import java.io.InputStream;
import java.util.List;

public interface StorageSession extends AutoCloseable {
    List<String> list(String remoteDir);
    InputStream download(String remotePath);
    void upload(String remotePath, InputStream content);
    @Override void close();
}
