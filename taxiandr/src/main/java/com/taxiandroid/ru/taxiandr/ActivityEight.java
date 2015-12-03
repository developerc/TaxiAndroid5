package com.taxiandroid.ru.taxiandr;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ActivityEight extends AppCompatActivity {
    ArrayList<StayArea> stayAreas;
    ListView lvEigth;
    CustomEigthAdapter adapter;
    String ClkStay;
    String postPath;
    private static final String TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);
        populateEigthList();
        lvEigth.setOnItemClickListener(itemClickListener);

        postPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/queue";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_eight, menu);

        return true;
    }

    private void populateEigthList() {
      stayAreas = StayArea.createAreas();
        adapter = new CustomEigthAdapter(this,stayAreas);
        lvEigth = (ListView) findViewById(R.id.lvStay);
        lvEigth.setAdapter(adapter);
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

    //обрабатываем нажатие на пункте списка заказов
    protected AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ClkStay = MainActivity.stay.get(position);
            Toast.makeText(getApplicationContext(), "Вы выбрали " + ClkStay, Toast.LENGTH_SHORT).show();
            new PostAsincTask().execute(postPath, "_method=", "delete");
            if (MyVariables.InOuExcept == false) new PostAsincTask().execute(postPath, "region_id=", MainActivity.id_stay.get(position));
        }
    };

    public class PostAsincTask extends AsyncTask<String, String,String> {
        String response = "";
        String errPost = "";

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, params[0]);
            String message = null;
            try {
                message = URLEncoder.encode(params[2], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");

                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(params[1] + message);
                writer.close();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    MyVariables.InOuExcept = false;
                     // Log.d(TAG, response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        errPost = jo.getString("error");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    MyVariables.InOuExcept = true;
                    Log.d(TAG,"Error code: " + String.valueOf(connection.getResponseCode()));
                }
            } catch (MalformedURLException e) {
                // ...
            } catch (IOException e) {
                MyVariables.InOuExcept = true;
            }

            return response;

        }
        @Override
        protected void onPostExecute(String result) {
            //получили JSON строку с сервера
            Log.d(TAG, response);
            if (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();
            } else {
                if (errPost.contains("none")) {
                    Toast.makeText(getApplicationContext(), "Сервер ответил ОК", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Сервер ответил Ошибка", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
