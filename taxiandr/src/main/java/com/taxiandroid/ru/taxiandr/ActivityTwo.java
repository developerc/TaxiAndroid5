package com.taxiandroid.ru.taxiandr;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class ActivityTwo extends AppCompatActivity {
    ArrayList<Two> arrayTwo;
    ListView lvTwo;
    CustomTwoAdapter adapter;
    TextView tvAdres;
    String postPath;
    private static final String TAG = "myLogs2";
    String errPost="";
    String httpSMS2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        tvAdres = (TextView) this.findViewById(R.id.tvAdres);
        tvAdres.setText(MainActivity.ClkAdr);
        populateTwoList();
        lvTwo.setOnItemClickListener(itemClickListener);

        postPath = MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/order/";
        httpSMS2 = MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/smsdriverarrived";
    }

    public class  TwoPostAsincTask extends AsyncTask<String, String, Void> {
        String response = "";
        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "*******************    Open Post Connection    *****************************");
            Log.d(TAG, params[0]);
            String url = params[0];
            JSONObject jsonBody;
            String requestBody;
            HttpURLConnection urlConnection = null;

            try {
                jsonBody = new JSONObject();
                //jsonBody.put("order_id", MainActivity.ClkZak);
                jsonBody.put(params[1], MainActivity.ClkZak);
                requestBody = jsonBody.toString();
                // requestBody = Utils.buildPostParameters(jsonBody);
                urlConnection = (HttpURLConnection) Utils.makeRequest("POST", url, null, "application/json", requestBody);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    MyVariables.InOuExcept = false;
                    Log.d(TAG, response);
                    try {
                        JSONObject jo =  new JSONObject(response);
                        errPost= jo.getString("error");
                        Log.d(TAG, String.valueOf(errPost));

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
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //получили JSON строку с сервера
            if  (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();} else {
                if (errPost.contains("none")) {
                    Toast.makeText(getApplicationContext(), "Сервер ответил ОК", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), ActivityTwo.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Сервер ответил Ошибка", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    //обрабатываем нажатие на пункте списка заказов
    protected AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
           // Two itemLV = (Two) parent.getItemAtPosition(position);
            String itemAct = null;
            //if (position ==1) {itemAct="Вы выбрали звонилку";}
            switch (position) {
                case 0: {
                    itemAct = "Вы выбрали Отправку СМС";
                    new TwoPostAsincTask().execute(httpSMS2,"nozak");
                }
                break;
                case 1: {
                    itemAct="Вы выбрали звонилку";
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    switch (MainActivity.ClkTel.length()) {
                        case 0:
                            intent.setData(Uri.parse("tel:" + "88619670000"));
                            break;
                        case 5:
                            intent.setData(Uri.parse("tel:"+"886196"+MainActivity.ClkTel));
                            break;
                        case 10:
                            intent.setData(Uri.parse("tel:"+"8"+MainActivity.ClkTel));
                            break;
                        default:
                            intent.setData(Uri.parse("tel:"+"88619670000"));
                    }
                    startActivity(intent);

                    Log.d(TAG,"Телефон: " + MainActivity.ClkTel);
                    Log.d(TAG, String.valueOf(MainActivity.ClkTel.length()));
                }
                break;
                case 2: {
                    itemAct="Вы выбрали таксометр";
                    startActivity(new Intent(getApplicationContext(),ActivityThree.class));
                    finish();
                }
                break;
                case 3: {
                    itemAct="Вы выбрали отказ";
                    new TwoPostAsincTask().execute(postPath + "delcar","order_id");
                    finish();
                }
            }

            Toast.makeText(getApplicationContext(),
                    itemAct, Toast.LENGTH_SHORT).show();
        }
    };

    public  class PostSMSAsincTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            return null;
        }
    }

        private void populateTwoList() {
        arrayTwo = Two.getTwoItem();
        adapter = new CustomTwoAdapter(this,arrayTwo);
        lvTwo = (ListView) findViewById(R.id.lvActTwo);
        lvTwo.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_two, menu);
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
}
