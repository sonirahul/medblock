package com.medblock.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FileAssets {

    @Id
    private String fileId;
    private String fileHashKey;
    private String fileName;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileHashKey() {
        return fileHashKey;
    }

    public void setFileHashKey(String fileHashKey) {
        this.fileHashKey = fileHashKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
