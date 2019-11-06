package com.chairul.sistemmonitoringbanjir.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseGetData {

    @SerializedName("date")
    private String date;

    @SerializedName("tinggi_air")
    private String tinggiAir;

    @SerializedName("intensitas_hujan")
    private String intensitas_Hujan;

    @SerializedName("id")
    private String id;

    @SerializedName("time")
    private String time;

    private ArrayList<String> data;

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public void setTinggiAir(String tinggiAir){
        this.tinggiAir = tinggiAir;
    }

    public String getTinggiAir(){
        return tinggiAir;
    }

    public String getIntensitas_Hujan() {
        return intensitas_Hujan;
    }

    public void setIntensitas_Hujan(String intensitas_Hujan) {
        this.intensitas_Hujan = intensitas_Hujan;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }
}
