package com.trinhbk.lecturelivestream.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by TrinhBK on 10/9/2018.
 */

public class FileExtension {

    @SerializedName("FileName")
    @Expose
    private String fileName;
    @SerializedName("FileSize")
    @Expose
    private Integer fileSize;
    @SerializedName("FileData")
    @Expose
    private String fileData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

}
