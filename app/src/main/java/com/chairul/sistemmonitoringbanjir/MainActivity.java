package com.chairul.sistemmonitoringbanjir;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.chairul.sistemmonitoringbanjir.Model.ResponseGetData;
import com.chairul.sistemmonitoringbanjir.network.RetroServer;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chairul.sistemmonitoringbanjir.Notify.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {

    public boolean isRepeat = true;

    private static final String TAG = "MainActivity";
    private static LineChart mChart, tChart;
    int n;
    TextView t1, t2, t3, t4;
    int[] dataID = new int[4000];
    int[] dataTinggi = new int[4000];
    float[] dataHujan = new float[4000];
    String[] dataTime = new String[4000];
    float[] times = new float[4000];
    float[] dtk = new float[4000];
    float[] mnt = new float[4000];
    float[] jam = new float[4000];
    float[] meanH = new float[4000];
    float[] meanT = new float[4000];
    float rangeT ;
    float rangeS ;
    float rataH = 0;
    float rataT = 0;
    float tWadah = 37;
    String waktu;

    TextView realH, realT;
    NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (LineChart) findViewById(R.id.LineChart);
        tChart = (LineChart) findViewById(R.id.TinggiChart);
        realH = (TextView) findViewById(R.id.realHujan);
        realT = (TextView) findViewById(R.id.realTinggi);


        mChart.setDragEnabled(true);
        tChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        tChart.setScaleEnabled(false);


        reloadOtomatis();
    }

    private void reloadOtomatis() {
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    while (!isInterrupted() && isRepeat) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadData();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRepeat = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRepeat = true;
        loadData();
    }

    private void loadData() {
        Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
        RetroServer.getInstance().getDataTable().enqueue(new Callback<List<ResponseGetData>>() {
            @Override
            public void onResponse(Call<List<ResponseGetData>> call, Response<List<ResponseGetData>> response) {
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        //clearArray();
                        Toast.makeText(MainActivity.this, "Data sebanyak : " + response.body().size(), Toast.LENGTH_SHORT).show();
                        n = response.body().size();
                        for (int i = 0; i < n; i++) {
                            dataID[i] = Integer.parseInt(String.valueOf(response.body().get(i).getId()));
                            dataTinggi[i] = Integer.parseInt(String.valueOf(response.body().get(i).getTinggiAir()));
                            dataTime[i] = response.body().get(i).getTime();

                            dataHujan[i] = Float.parseFloat(String.valueOf(response.body().get(i).getIntensitas_Hujan()));


                        }

                        realtime();
                    } else {
                        Toast.makeText(MainActivity.this, "data tidak ada", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<ResponseGetData>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }


    private void realtime() {
        realH.setText("Real Time Hujan => Time : " + dataTime[n - 1] + "Hujan : " + dataHujan[n - 1]);
        realT.setText("Real Time Tinggi => Time : " + dataTime[n - 1] + "Tinggi : " + dataTinggi[n - 1]);
        chart();
    }



        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<Entry> tValues = new ArrayList<>();

        private void chart(){
            yValues.clear();
            tValues.clear();

            for (int i = 0; i < n; i++){
                dtk[i] = Float.parseFloat(dataTime[i].substring(6,8) + ".0");
                mnt[i] = Float.parseFloat(dataTime[i].substring(3,5)+ ".0");
                jam[i] = Float.parseFloat(dataTime[i].substring(0,2)+ ".0");

                times[i] = (dtk[i] + mnt[i] * 60) / 3600 + jam[i]   ;
            }

            yValues = new ArrayList<>();
            tValues = new ArrayList<>();

            int jum = 0;
            for (int i = 1; i < n ; i++){


                if (dtk[i] == dtk[0] - 2 || dtk[i] == dtk[0] -1 )  {

                    yValues.add(new Entry(times[i], dataHujan[i]));
                    tValues.add(new Entry(times[i], dataTinggi[i]));

                    if(mnt[i] < mnt[0] + 11) {
                        meanH[jum] = dataHujan[i];
                        meanT[jum] = dataTinggi[i];
                    }

                    if (jum == 9 ){
                        waktu = dataTime[i];
                    }
                    jum++;
                }
            }

            yValues.add(new Entry(times[n-1], dataHujan[n-1]));
            tValues.add(new Entry(times[n-1], dataTinggi[n-1]));

            LineDataSet set1 = new LineDataSet(yValues, "Curah Hujan (mm of rain)");
            LineDataSet set2 = new LineDataSet(tValues, "Tinggi Air (cm)");

            set1.setFillAlpha(110);
            set2.setFillAlpha(110);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            ArrayList<ILineDataSet> tinggiSets = new ArrayList<>();
            dataSets.add(set1);
            tinggiSets.add(set2);

            LineData data = new LineData(dataSets);
            LineData tinggiData = new LineData(tinggiSets);

            mChart.setData(data);
            mChart.invalidate();
            tChart.setData(tinggiData);
            tChart.invalidate();

            notif();
        }


    private void notif() {

        for (int i = 0; i < 10; i++){

            rataH = rataH + meanH[i];

        }

        rataH = rataH / 10;
        rataT = meanT[9] / 10 ;

        rangeS = tWadah - meanT[9];
        rangeT = rangeS / rataT;
        String perkiraan = String.valueOf(rangeT).substring(0, 4);


        Toast.makeText(MainActivity.this, "Rata Hujan  = " + rataH + "  Rata Tinggi = " + rataT +"  Jarak Tinggi Air = " + rangeS + "Perkiraan Air Meluap dalam waktu " + rangeT + " menit lagi", Toast.LENGTH_SHORT).show();

        notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Time : "+waktu)
                .setContentText("Perkiraan Air Meluap Dalam Waktu : "+perkiraan+" Menit Lagi")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
        rataH = 0;
        rataT = 0;
    }


}
