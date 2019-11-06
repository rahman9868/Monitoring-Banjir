package com.chairul.sistemmonitoringbanjir.network;

import com.chairul.sistemmonitoringbanjir.Model.ResponseGetData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("json.php")
    Call<List<ResponseGetData>> getDataTable();
}
