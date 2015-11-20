package com.taxiandroid.ru.taxiandr;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityThree extends AppCompatActivity {
    public static Button buttonStart;
    Button buttonStop;
    public static TextView tvDistance, tvStay;

    // static boolean StartTax=false;
    //static boolean FirstTime=true;
    //static boolean PauseTax=true;
    Calendar cal;
    private static final String TAG = "myLogs";
    final Handler myHandler = new Handler();
    String postPath;
    String errPost = "";
    private static DatabaseHelper mDatabaseHelper;
    // MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        buttonStart = (Button) this.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickStart();
            }
        });

        buttonStop = (Button) this.findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clickStop();
            }
        });
        tvStay = (TextView) this.findViewById(R.id.tvStay);
        tvDistance = (TextView) this.findViewById(R.id.tvDistance);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, 5000);

        mDatabaseHelper = new DatabaseHelper(this);

        postPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/order/";
    }

    private void clickStart() {
        //  buttonStart.setText("29,00 руб");
        MainActivity.StartTax = true; //таксометр запущен
        if (MainActivity.PauseTax == true) {
            MainActivity.PauseTax = false;
            //MainActivity.flagPause=0;
            // btnStart.setBackgroundColor(Color.GREEN);
            Toast.makeText(getApplicationContext(), "Начато начисление", Toast.LENGTH_SHORT).show();
            if (MainActivity.FirstTime) { //начисляем посадочные
                cal = Calendar.getInstance();
                try {
                    if (PosadkaDay()) {
                        if (MainActivity.ClkPre.equalsIgnoreCase("YES"))
                            MainActivity.Itogo = MainActivity.Itogo + MyVariables.cost_passenger_pre_boarding_day;
                        else
                            MainActivity.Itogo = MainActivity.Itogo + MyVariables.cost_passenger_boarding_day;

                    } else {
                        if (MainActivity.ClkPre.equalsIgnoreCase("YES"))
                            MainActivity.Itogo = MainActivity.Itogo + MyVariables.cost_passenger_pre_boarding_night;
                        else
                            MainActivity.Itogo = MainActivity.Itogo + MyVariables.cost_passenger_boarding_night;
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            MainActivity.PauseTax = true;
        }
//        MainActivity.StartTax=true; //таксометр запущен
    }

    private void clickStop() {
        // шлем запрос на удаление заказа из таблицы заказов
        new PostAsincTask().execute(postPath + "complete");
        mDatabaseHelper.addTaximeter(new Taximeter(MainActivity.ClkTel,MainActivity.ClkAdr, Float.toString(MainActivity.Itogo) +" руб", Float.toString(MainActivity.ItogKmGorod)+" м гор", Float.toString(MainActivity.ItogKmPrig)+" м пригор",getCurrentTimeStamp())); //добавляем запись в базу
        Log.d(TAG, "*******************    Добавляем в базу    *****************************");
       // mDatabaseHelper.addTaximeter(new Taximeter("8619641256", "Адрес", "100", "2015-11-20 11:00")); //добавляем запись в базу

        MainActivity.StartTax = false; //таксометр остановлен
        MainActivity.PauseTax = true;
        MainActivity.FirstTime = true;
        MainActivity.OldTime = 0;
        MainActivity.NewTime = 0;
        MainActivity.TimeInterval = 0;
        MainActivity.TimeItogo = 0;
        MainActivity.TimeStay = 0;
        MainActivity.OldSpeed = 0;
        MainActivity.NewSpeed = 0;
        MainActivity.AverageSpeed = 0;
        MainActivity.Distance = 0;
        MainActivity.Cost = 0;
        MainActivity.ItogKmGorod = 0;
        MainActivity.ItogKmPrig = 0;
        MainActivity.ItogKmRn = 0;
        MainActivity.ItogKmMg = 0;
        MainActivity.Itogo = 0;
        finish();
        /*mediaPlayer = MediaPlayer.create(this, R.raw.sound_5);
        mediaPlayer.start();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_three, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static public boolean PosadkaDay() throws ParseException {
        //находим днем посадка или ночью
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        cal.setTime(currentTime);

        if (cal.get(Calendar.HOUR_OF_DAY) >= 6 & cal.get(Calendar.HOUR_OF_DAY) < 22) return true;
        else return false;
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
    }

    private void UpdateGUI() {
        // i++;
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
            //Toast.makeText(getApplicationContext(), "My Runnable!", Toast.LENGTH_SHORT).show();
            if (MainActivity.StartTax == true) {
                if (MainActivity.PauseTax == false) {
                    buttonStart.setText(String.format("%.2f" + " руб", MainActivity.Itogo));
                    tvDistance.setText("Дистанция: " + String.format("%.1f", MainActivity.ItogKmGorod + MainActivity.ItogKmPrig + MainActivity.ItogKmRn + MainActivity.ItogKmMg) + " м");

                    tvStay.setText("Стоянка: " + MainActivity.hms);
                } else buttonStart.setText("Пауза");
            }

        }
    };

    class PostAsincTask extends AsyncTask<String, Void, Void> {
        String response = "";

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "******************* Отправляю Post на удаление заказа    *****************************");
            String url = params[0];
            JSONObject jsonBody;
            String requestBody;
            HttpURLConnection urlConnection = null;
            try {
                jsonBody = new JSONObject();
                jsonBody.put("order_id", MainActivity.ClkZak);
                jsonBody.put("cost", "100");
                requestBody = jsonBody.toString();
                urlConnection = (HttpURLConnection) Utils.makeRequest("POST", url, null, "application/json", requestBody);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    MyVariables.InOuExcept = false;
                    Log.d(TAG, response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        errPost = jo.getString("error");
                        Log.d(TAG, errPost);
                        /*if (errPost.contains("none")) {
                            Toast.makeText(getApplicationContext(), "Сервер ответил ОК", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Сервер ответил Ошибка", Toast.LENGTH_SHORT).show();
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Log.d(TAG, String.valueOf(errPost));
                } else {
                    // Toast.makeText(getApplicationContext(), "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, String.valueOf(urlConnection.getResponseCode()));
                }

            } catch (JSONException | IOException e) {
                MyVariables.InOuExcept = true;
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }


                return null;
            }
        }
    }

}
