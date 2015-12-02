package com.taxiandroid.ru.taxiandr;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
            new PostAsincTask().execute(postPath, "_method","delete");
        }
    };

    public class PostAsincTask extends AsyncTask<String, String,String> {
        String response = "";
        String errPost = "";

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, params[0]);
            String url = params[0];
            JSONObject jsonBody;
            String requestBody;
            HttpURLConnection urlConnection = null;
            try {
                jsonBody = new JSONObject();
                jsonBody.put(params[1], params[2]);
                requestBody = jsonBody.toString();
                // requestBody = Utils.buildPostParameters(jsonBody);
                urlConnection = (HttpURLConnection) Utils.makeRequest("POST", url, null, "application/json", requestBody);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    MyVariables.InOuExcept = false;
                    // Log.d(TAG, response);
                    try {
                        JSONObject jo = new JSONObject(response);
                        errPost = jo.getString("error");
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
                //...
                // return response;
            } catch (JSONException | IOException e) {
                MyVariables.InOuExcept = true;
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
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
