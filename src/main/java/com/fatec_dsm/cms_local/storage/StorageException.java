package com.fatec_dsm.cms_local.storage;

public class StorageException extends RuntimeException {
    private final int replyCode;

    public StorageException(String message, int replyCode, Throwable cause){
        super(message, cause);
        this.replyCode = replyCode;
    }
    public int getReplyCode(){
        return replyCode;
    }
}
