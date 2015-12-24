package com.taxiandroid.ru.taxiandr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class FourActivity extends AppCompatActivity {
    Button btnSave, btnLoad, btnUpdate;
    EditText etLgn, etPsw, etSrv;
    SharedPreferences sPref;
    CheckBox cb;
    String postPath;
    private static final String TAG = "myLogs";
    private TextView tvInfo;
    private ProgressBar progressDownload;
    private File dlDir;


    final String SAVED_TEXT_LGN = "saved_text_lgn";
    final String SAVED_TEXT_PSW = "saved_text_psw";
    final String HAND_STAY = "hand_stay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        postPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/mpinq";
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        progressDownload = (ProgressBar) findViewById(R.id.progressDownload);

        etLgn = (EditText) this.findViewById(R.id.etLgn);
        etPsw = (EditText) this.findViewById(R.id.etPsw);
        etSrv = (EditText) this.findViewById(R.id.etSrv);

        btnSave = (Button) this.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveText();

            }
        });

        btnLoad = (Button) this.findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadText();
            }
        });

        btnUpdate =(Button) this.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                updateProg();
            }
        });

        sPref = this.getSharedPreferences("pref",0);
        MyVariables.SAVED_TEXT_1 = sPref.getString(SAVED_TEXT_LGN, "");
        MyVariables.SAVED_TEXT_2 = sPref.getString(SAVED_TEXT_PSW, "");

        cb=(CheckBox) findViewById(R.id.checkBox1);
        sPref = this.getSharedPreferences("pref",0);
        cb.setChecked(sPref.getBoolean(HAND_STAY, false));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Вы выбрали ручную постановку" , Toast.LENGTH_SHORT).show();
                    new PostAsincTask().execute(postPath,"1");
                    setHandStay(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Вы выбрали автоматическую постановку" , Toast.LENGTH_SHORT).show();
                    new PostAsincTask().execute(postPath, "0");
                    setHandStay(false);
                }
            }
            });
    }

    private void setHandStay(boolean handStay){
        sPref = this.getSharedPreferences("pref", 0);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(HAND_STAY, handStay);
        ed.commit();
    }


    private void saveText() {
        sPref = this.getSharedPreferences("pref", 0);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT_LGN, etLgn.getText().toString());
        ed.putString(SAVED_TEXT_PSW, etPsw.getText().toString());
        ed.commit();
        Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();

        MyVariables.SAVED_TEXT_1=etLgn.getText().toString();
        MyVariables.SAVED_TEXT_2=etPsw.getText().toString();
        MyVariables.HTTPAdress = etSrv.getText().toString();
    }

    private void loadText() {
        sPref = this.getSharedPreferences("pref",0);
        String savedTextLgn = sPref.getString(SAVED_TEXT_LGN, "");
        String savedTextPsw = sPref.getString(SAVED_TEXT_PSW, "");

        etLgn.setText(savedTextLgn);
        etPsw.setText(savedTextPsw);
        etSrv.setText(MyVariables.HTTPAdress);
        Toast.makeText(this, "Загружено " , Toast.LENGTH_SHORT).show();

        MyVariables.SAVED_TEXT_1=savedTextLgn;
        MyVariables.SAVED_TEXT_2=savedTextPsw;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_four, menu);
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
                jsonBody.put("mpinq", params[1]);
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

    public void updateProg() {
        String urlImage = "http://pchelka.teleknot.ru/TD25.apk";
        dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Toast toast = Toast.makeText(getBaseContext(),
                "Загружаем в директорию: " + dlDir.toString(), Toast.LENGTH_LONG);
        toast.show();
        new GetProgTask().execute(urlImage);
    }

    private class GetProgTask extends AsyncTask<String, Integer, String> {
        protected void onPreExecute() {
            progressDownload.setProgress(0);
        }

        @Override
        protected String doInBackground(String... urls) {

            String filename = "TD25.apk";
            File myFile = new File(dlDir, filename);

            try {
                URL url = new URL(urls[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int fileSize = connection.getContentLength();

                InputStream is = new BufferedInputStream(url.openStream());
                OutputStream os = new FileOutputStream(myFile);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = is.read(data)) != -1) {
                    total += count;
                    publishProgress((int) (total * 100 / fileSize));
                    os.write(data, 0, count);
                }

                os.flush();
                os.close();
                is.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return filename;
        }
        protected void onProgressUpdate(Integer... progress) {
            tvInfo.setText(String.valueOf(progress[0]) + "%");
            progressDownload.setProgress(progress[0]);
        }

        protected void onCancelled() {
            Toast toast = Toast.makeText(getBaseContext(),
                    "Error connecting to Server", Toast.LENGTH_LONG);
            toast.show();
        }
        protected void onPostExecute(String filename) {
            progressDownload.setProgress(100);
            tvInfo.setText("Загрузка завершена...");

            dlDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //начинаем установку приложения
            File file = new File(dlDir, filename);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(Uri.fromFile(file));
            startActivity(intent);
            }

    }
}
