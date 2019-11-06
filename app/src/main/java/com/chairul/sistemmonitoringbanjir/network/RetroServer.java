package com.chairul.sistemmonitoringbanjir.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroServer {
    private static final String base_url = "http://18.212.62.143/ ";

    private static Retrofit setInit(){
        return new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiService getInstance(){
        return setInit().create(ApiService.class);
    }
}
