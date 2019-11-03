package com.trinhbk.lecturelivestream.network;

import com.trinhbk.lecturelivestream.network.response.FileResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by TrinhBK on 10/9/2018.
 */

public interface LiveSiteService {


    @Multipart
    @POST("pdf/to/png?Secret=iEBPvNXpjZtILiTi")
    Call<FileResponse> uploadFilePDF(@Part MultipartBody.Part file);

    @Multipart
    @POST("ppt/to/png?Secret=iEBPvNXpjZtILiTi")
    Call<FileResponse> uploadFilePPT(@Part MultipartBody.Part file);

    @Multipart
    @POST("pptx/to/png?Secret=iEBPvNXpjZtILiTi")
    Call<FileResponse> uploadFilePPTX(@Part MultipartBody.Part file);
}
