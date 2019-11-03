package com.trinhbk.lecturelivestream.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FileResponse {

    @SerializedName("ConversionCost")
    @Expose
    private Integer conversionCost;
    @SerializedName("Files")
    @Expose
    private List<FileExtension> files = null;

    public Integer getConversionCost() {
        return conversionCost;
    }

    public void setConversionCost(Integer conversionCost) {
        this.conversionCost = conversionCost;
    }

    public List<FileExtension> getFiles() {
        return files;
    }

    public void setFiles(List<FileExtension> files) {
        this.files = files;
    }

}