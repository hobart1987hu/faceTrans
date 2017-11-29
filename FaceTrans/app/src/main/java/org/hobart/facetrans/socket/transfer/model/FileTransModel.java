package org.hobart.facetrans.socket.transfer.model;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class FileTransModel extends TransModel {

    private String fileName;

    private long fileSize;

    private String filePath;

    private boolean isZipFile;


    //    private String savePath;

    private String id;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

//    public String getSavePath() {
//        return savePath;
//    }
//
//    public void setSavePath(String savePath) {
//        this.savePath = savePath;
//    }


    public boolean isZipFile() {
        return isZipFile;
    }

    public void setZipFile(boolean zipFile) {
        isZipFile = zipFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
