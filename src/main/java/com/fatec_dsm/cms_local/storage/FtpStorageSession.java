package com.fatec_dsm.cms_local.storage;

import org.apache.commons.net.ftp.FTPClient;
import java.io.IOException;

class FtpStorageSession implements StorageSession {
    private final FTPClient client;

    FtpStorageSession(FTPClient client){
        this.client = client;
    }
    @Override
    public java.util.List<String> list(String remoteDir) {
        try{
            return java.util.List.of(client.listNames(remoteDir));
        }catch (IOException e){
            throw new StorageException ("Listagem (LIST) falhou em " + remoteDir, client.getReplyCode(), e);
        }
    }

    @Override
    public java.io.InputStream download(String remotePath) {
        throw new UnsupportedOperationException("O download chega em 2.2");
    }

    @Override
    public void upload(String remotePath, java.io.InputStream content) {
        throw new UnsupportedOperationException("O upload chega em 2.3");
    }

    @Override
    public void close() {
        try {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }
        } catch (IOException ignored) {
            // melhor maneira possível de limpeza: close() nunca deve mascarar o resultado real da operação _  // best-effort cleanup: close() must never mask the operation's real outcome
        }
    }
}
