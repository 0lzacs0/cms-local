package com.fatec_dsm.cms_local.storage;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class FtpStorageService implements StorageService{
    private static final Logger log = LoggerFactory.getLogger(FtpStorageService.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public FtpStorageService(String host, int port, String username, String password){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

    }

    @Override
    public StorageSession open(){
        FTPClient client = new FTPClient();   //gerado a cada uso – nunca um campo _ born per use - never a field
        try{
            client.connect(host, port);
            int reply = client.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect();
                throw new StorageException("Conexão recusada por " + host + ":" + port, reply, null);
            }
        }catch (IOException e){
            throw new StorageException("Não consegue contatar " + host + ":" + port, 0, e);
        }

        boolean loggedIn;
        try {
            loggedIn = client.login(username, password);
        } catch (IOException e) {
            try {
                client.disconnect();
            } catch (IOException ignored) { }
            throw new StorageException("Login falhou no " + host + ":" + port, 0, e);
        }

        if(!loggedIn){
            int reply = client.getReplyCode();
            try {
                client.disconnect();
            } catch (IOException ignored) { }
            throw new StorageException("Login rejeitado para " + host + ":" + port, reply, null);
        }

        log.info("Sessão de FTP aberta para {}:{}", host, port);
        return new FtpStorageSession(client);
    }
}