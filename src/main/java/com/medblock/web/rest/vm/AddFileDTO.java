package com.medblock.web.rest.vm;

public class AddFileDTO {

    private String fileHashKey;
    private String key;
    private String phoneNumber;
    private String fileId;

    public String getFileHashKey() {
        return fileHashKey;
    }

    public void setFileHashKey(String fileHashKey) {
        this.fileHashKey = fileHashKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}
