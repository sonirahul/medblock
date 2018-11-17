package com.medblock.web.rest.vm;

public class GetFilesVM {

    private String fileId;
    private String fileIpfsHash;
    private String fileName;
    private boolean permitted;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileIpfsHash() {
        return fileIpfsHash;
    }

    public void setFileIpfsHash(String fileIpfsHash) {
        this.fileIpfsHash = fileIpfsHash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isPermitted() {
        return permitted;
    }

    public void setPermitted(boolean permitted) {
        this.permitted = permitted;
    }

    @Override
    public String toString() {
        return "GetFilesVM{" +
            "fileId='" + fileId + '\'' +
            ", fileIpfsHash='" + fileIpfsHash + '\'' +
            ", fileName='" + fileName + '\'' +
            ", permitted=" + permitted +
            '}';
    }
}
