package com.taxiandroid.ru.taxiandr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    int i = 0;
    ArrayList<Orders> arrayOfOrders;
    ListView lvOrders;
    CustomOrdersAdapter adapter;
    String httpPath;
    String postPath;
    String httpTaxometrPath;
    final Handler myHandler = new Handler();
    // Runnable runnable;
    // HTTGATask httgatask;  //обьявили класс для метода GEt
    private static final String TAG = "myLogs";
    final String textSource = "http://pchelka.teleknot.ru/api/user1/x11unkde/orders";
    public static boolean ZakazEmpty = true;
    SharedPreferences sPref;
    final String SAVED_TEXT_LGN = "saved_text_lgn";
    final String SAVED_TEXT_PSW = "saved_text_psw";
    public static boolean flagClkLV = false;
    static String errPost = "";
    public static int ClkZak;
    public static String ClkAdr = "";
    public static String ClkTel = "";
    public static String ClkPre = "";

    public static ArrayList<Integer> plyzak = new ArrayList<Integer>();
    public static ArrayList<Integer> zakaz = new ArrayList<Integer>();
    public static ArrayList<String> telefon = new ArrayList<String>();
    public static ArrayList<String> kode = new ArrayList<String>();
    public static ArrayList<String> dat = new ArrayList<String>();
    public static ArrayList<String> tim = new ArrayList<String>();
    public static ArrayList<String> adres = new ArrayList<String>();
    public static ArrayList<String> car = new ArrayList<String>();
    public static ArrayList<String> predvar = new ArrayList<String>();
    public static ArrayList<String> stay = new ArrayList<String>();

    HTTGATaxometr httgataxometr; //обьявили класс для настроек таксометра
    private static final String ERROR = "error";    //получаем JSON текст с параметрами error
    private static final String RESULT = "result";  //и result
    private LocationManager locationManager;
    static boolean FlagAppStarted = false;
    private Context context;

    //переменные для таксометра
    String Lat;
    String Lon;
    public static long OldTime=0, NewTime=0, TimeInterval, TimeItogo=0, TimeStay=0;
    public static float OldSpeed, NewSpeed, AverageSpeed, Distance, Cost, ItogKmGorod, ItogKmPrig, ItogKmRn, ItogKmMg=0;
    public static float Itogo=0;
    boolean tutGorod=false; //в городе
    boolean tutVG=false;    //на военном городке
    boolean tutPG=false;    //в пригороде
    boolean tutRn=false;    //в районе
    boolean tutRn2=false;    //тоже в районе
    public static double d;
    public static double da;
    public static double db;
    public static double ta;
    public static double tb;
    public static double XIntersect;
    public static double YIntersect;

    static boolean StartTax=false;
    //static boolean StartTax=true;
    static boolean FirstTime=true;
    static boolean PauseTax=true;
    static String hms;

    MenuItem item2, item3;
    MediaPlayer mediaPlayer;
    boolean flagMusYes = false;
    static boolean GettingZak = false;

   // Intent myIntent;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        populateUsersList();
        lvOrders.setOnItemClickListener(itemClickListener);

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateGUI();
            }
        }, 0, 15000);

        sPref = this.getSharedPreferences("pref", 0);
        MyVariables.SAVED_TEXT_1 = sPref.getString(SAVED_TEXT_LGN, "");
        MyVariables.SAVED_TEXT_2 = sPref.getString(SAVED_TEXT_PSW, "");

        httpPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/orders";
        postPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/order/";
        httpTaxometrPath = MyVariables.HTTPAdress + MyVariables.SAVED_TEXT_1 + "/" + MyVariables.SAVED_TEXT_2 + "/taximeter";

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        context = getApplicationContext();
        Log.d(TAG, "Запустилось! ");
       // myIntent = new Intent(getApplicationContext(), ActivityThree.class);
        //здесь заполним ArrayList stay значениями стоянок, потом будем его заполнять значениями из JSON-а
        stay.add("вне зоны");
        stay.add("Черемушки");
        stay.add("Центр");
        stay.add("Та сторона");
        stay.add("Парковый");
    }

    private void UpdateGUI() {
        i++;
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        public void run() {
           // Log.d(TAG, "Таймер работает! ");
            if (i != 1) { // если не первый раз
                if (flagClkLV == false) { //если не кликнули на ListView
                    double minost=i%4;
                    if (minost==0)  //каждую минуту отсылаем координаты машины
                       new LonLatAsincTask().execute(MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/setcoord");
                    else new GetAsincTask().execute(httpPath); //или получаем список заказов
                    if (ZakazEmpty == false) {
                        updateUsersList(); //обновляем ListView
                        PlyNewZak();
                    } else {
                        populateUsersList();
                    }
                } else { //отсылаем POST на взятие заказа
                    new PostAsincTask().execute(postPath + "addcar");
                }
            } else { //первый раз получаем настройки таксометра
                new HTTGATaxometr().execute(httpTaxometrPath);
            }

        }
    };

    public class PostAsincTask extends AsyncTask<String, Void, String> {
        String response = "";

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "*******************    Open Post Connection    *****************************");
            Log.d(TAG, params[0]);
            String url = params[0];
            JSONObject jsonBody;
            String requestBody;
            HttpURLConnection urlConnection = null;

            try {
                jsonBody = new JSONObject();
                jsonBody.put("order_id", ClkZak);
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
                    Log.d(TAG, response);
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
            // return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //получили JSON строку с сервера
            // Log.d(TAG, textResult);
            //обрабатываем JSON строку

            flagClkLV = false;
            super.onPostExecute(result);
            if (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();
            } else {
                if (errPost.contains("none")) {
                    //Toast.makeText(getApplicationContext(), "Сервер ответил ОК", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), ActivityTwo.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Сервер ответил Ошибка", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public class GetAsincTask extends AsyncTask<String, Void, Void> {

        String textResult;

        @Override
        protected Void doInBackground(String... params) {
            GettingZak = true;

            try {
                Log.d(TAG, "*******************    Open Connection    *****************************");
                URL url = new URL(params[0]);
                Log.d(TAG, "Received URL:  " + url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                // Log.d(TAG, "The response is: " + response);
                InputStream in = conn.getInputStream();
                // Log.d(TAG, "GetInputStream:  " + in);

                // Log.d(TAG, "*******************    String Builder     *****************************");
                String line = null;

                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String StringBuffer;
                String stringText = "";
                while ((StringBuffer = bufferReader.readLine()) != null) {
                    stringText += StringBuffer;
                }
                MyVariables.InOuExcept = false;
                bufferReader.close();

                textResult = stringText;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                MyVariables.InOuExcept = true;
                e.printStackTrace();
                textResult = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //получили JSON строку с сервера
            // Log.d(TAG, textResult);
            //обрабатываем JSON строку
            if (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    ZakazJson(textResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            GettingZak = false;

            super.onPostExecute(result);
        }
    }  //Закончился GetAsincTask


    //обработка JSON строки
    public void ZakazJson(String jsonString) throws JSONException {

        plyzak.clear();
        Log.d(TAG, "*******************    обрабатываем JSON строку     *****************************");
        if (jsonString.contains("ERROR: zakazi not found")) {
            ZakazEmpty = true;
            plyzak.clear();
            Log.d(TAG, "Заказов нет");
        } else {
            Log.d(TAG, "Заказы есть");
            Log.d(TAG, "");
            ZakazEmpty = false;
            // jsonString = "{\"myjsonarray\"="+jsonString+"}";
            Log.d(TAG, jsonString);
            // JSONObject jo =  new JSONObject(jsonString);
            // JSONArray jsonMainArr = null;
            //jsonMainArr = jo.toJSONArray(jsonMainArr);
            // JSONArray jsonMainArr = jo.getJSONArray("myjsonarray");
            JSONArray jsonMainArr = new JSONArray(jsonString);

             //запомнили номера прошлых заказов
            plyzak.clear();
            for (int i=0; i<zakaz.size(); i++) plyzak.add(zakaz.get(i));
            //for (int i=0; i<plyzak.size(); i++) Log.d(TAG, Integer.toString(plyzak.get(i)));
            //Очищаем ArrayList
            zakaz.clear();
            telefon.clear();
            kode.clear();
            dat.clear();
            tim.clear();
            adres.clear();
            car.clear();
            predvar.clear();

            for (int i = 0; i < jsonMainArr.length(); i++) {
                JSONObject json_data = jsonMainArr.getJSONObject(i);
                zakaz.add(json_data.getInt("zakaz"));
                telefon.add(json_data.getString("telefon"));
                kode.add(json_data.getString("kode"));
                dat.add(json_data.getString("dat"));
                tim.add(json_data.getString("tim"));
                adres.add(json_data.getString("adres"));
                car.add(json_data.getString("car"));
                predvar.add(json_data.getString("predvar"));
                // Log.d(TAG, "Заказ=" + zakaz.get(i) + "  Адрес:" + adres.get(i) + "  Предварительный:" + predvar.get(i));

            }
            for (int i = 0; i < zakaz.size(); i++) {

                Log.d(TAG, "Заказ=" + zakaz.get(i) + "  Адрес:" + adres.get(i) + "  Предварительный:" + predvar.get(i) + " Дата:" + dat.get(i) + " Время:" + tim.get(i).substring(11, 16) + " Машина:" + car.get(i));
            }
            /*mediaPlayer = MediaPlayer.create(this, R.raw.sound_5);
            mediaPlayer.start();*/
        }
//-----------------------------------------------------------------------------------------
      /*  boolean flagsred2 = false;
        boolean flagsred = false;
        boolean flagPlyMus = false;

        for (int i=0; i<zakaz.size(); i++) {

            for (int k=0; k<plyzak.size(); k++) {
                if (plyzak.get(k)==zakaz.get(i)) flagsred = true;
                else flagsred =false;

                Log.d(TAG, "plyzak="+Integer.toString(plyzak.get(k)) +"  " + "zakaz="+Integer.toString(zakaz.get(i)) +"  flagsred=" + flagsred);
                flagsred2=flagsred2|flagsred;
                Log.d(TAG, "flagsred2=" + flagsred2);
            }
            if (flagsred2==false & ZakazEmpty==false) flagPlyMus=true;
            Log.d(TAG, "------------flagsred2=" + flagsred2);
            flagsred2=false;
        }
        Log.d(TAG, "------------flagPlyMus=" + flagPlyMus);
        flagMusYes=flagPlyMus;*/
       // Log.d(TAG, "------------flagMusYes=" + flagMusYes);
    }  //конец ZakazJson

    /*class HTTGATask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                ShedZapros(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {  //выводим результат в GUI
            super.onPostExecute(result);

        }

        private void ShedZapros(String myurl) throws IOException {//зашедуленый запрос на прием заказа
            InputStream is = null;

            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 );
                conn.setConnectTimeout(15000 );
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("TAG", "The response is: " + response);
                is = conn.getInputStream();

            } finally {
                if (is != null) {
                    is.close();
                }
            } //конец ShedZapros
        }
    }*/

    //обрабатываем нажатие на пункте списка заказов
    protected AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            view.setSelected(true);
            // Orders itemLV = (Orders) parent.getItemAtPosition(position);
            //String itemZak = itemLV.adres;
            ClkZak = zakaz.get(position);
            ClkAdr = adres.get(position);
            ClkTel = telefon.get(position);
            ClkPre = predvar.get(position);
            Toast.makeText(getApplicationContext(),
                    "Вы выбрали " + ClkAdr + " \n заказ " + ClkZak, Toast.LENGTH_SHORT).show();
            if (flagClkLV == false) {
                flagClkLV = true;
            } else flagClkLV = false;
            //потом откроем Activity2 если придет респонз об удачном взятии заказа
            // startActivity(new Intent(getApplicationContext(),ActivityTwo.class));
        }
    };

    private void populateUsersList() {
        // Construct the data source
        arrayOfOrders = Orders.getOrders();
        // Create the adapter to convert the array to views
        adapter = new CustomOrdersAdapter(this, arrayOfOrders);
        lvOrders = (ListView) findViewById(R.id.lvOrders);
        lvOrders.setAdapter(adapter);
    }

    private void updateUsersList() {
        // Construct the data source
        // ArrayList<User> arrayOfUsers = User.getUsers();
        arrayOfOrders = Orders.UpdateOrders();
        // Create the adapter to convert the array to views
        adapter = new CustomOrdersAdapter(this, arrayOfOrders);
        // Attach the adapter to a ListView
        lvOrders = (ListView) findViewById(R.id.lvOrders);
        lvOrders.setAdapter(adapter);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                // startActivity(new Intent(getApplicationContext(),FourActivity.class));
                break;
            case 2:
                //mTitle = getString(R.string.title_section2);
                startActivity(new Intent(getApplicationContext(), FourActivity.class));
                break;
            case 3:
                //mTitle = getString(R.string.title_section3);
                startActivity(new Intent(getApplicationContext(), ActivityFive.class));
                break;
            case 4:
               // mTitle = getString(R.string.title_section4);
                startActivity(new Intent(getApplicationContext(), ActivitySix.class));
                break;
            case 5:
                //mTitle = getString(R.string.title_section5);
                //myIntent = new Intent(getApplicationContext(), ActivityThree.class);

                startActivity(new Intent(getApplicationContext(),ActivityThree.class));
                //startActivity(myIntent);
                break;
            case 6:
                startActivity(new Intent(getApplicationContext(), ActivitySeven.class));
                break;
            case 7:
                //finish();
                startActivity(new Intent(getApplicationContext(), ActivityEight.class));
                break;
            case 8:
                finish();
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            item2 = menu.findItem(R.id.item2); //нашли меню итем
            item3 = menu.findItem(R.id.item3); //нашли меню итем
            /*item4 = menu.findItem(R.id.item4); //нашли меню итем
            item5 = menu.findItem(R.id.item5); //нашли меню итем
            */
            iconINETyes();
            iconINETno();
            iconGPSyes();
            iconGPSno();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    class HTTGATaxometr extends AsyncTask<String, Void, Void> { //получаем первоначальные настройки таксометра

        @Override
        protected Void doInBackground(String... arg0) {
            // отсылаем GET запрос на получение настроек таксометра
            try {
                GetZapros();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {  //выводим результат в переменные класса MyVariables
            super.onPostExecute(result);
            // Log.d(TAG, "cost_km_city:  " + MyVariables.cost_km_city);
            if (MyVariables.cost_km_city == 0.0) i = 0;

            if (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();
            }
        }

    } //конец класса HTTGAtask

    private void GetZapros() throws MalformedURLException {
        Log.d(TAG, "*******************    Получаем настройки таксометра    *****************************");
        URL url = new URL(httpTaxometrPath);
        Log.d(TAG, "Received URL:  " + url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            // Log.d(TAG, "The response is: " + response);
            InputStream in = conn.getInputStream();
            String line = null;

            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String StringBuffer;
            String stringText = "";
            while ((StringBuffer = bufferReader.readLine()) != null) {
                stringText += StringBuffer;
            }
            MyVariables.InOuExcept = false;
            bufferReader.close();
            MyVariables.InOuExcept = false;
            Log.d(TAG, stringText);
            //---------- разбираем JSON строку.
            try {
                JSONObject jo = new JSONObject(stringText);
                if (jo.getString(ERROR).equals("none")) { //если нет ошибок
                    if (jo.getString(RESULT).equals("null")) {//если результат нулевой
                        MyVariables.cost_km_city = 0;
                        MyVariables.cost_km_intercity = 0;
                        MyVariables.cost_km_suburb = 0;
                        MyVariables.cost_km_n1 = 0;
                        MyVariables.cost_stopping = 0;
                        MyVariables.cost_passenger_boarding_day = 0;
                        MyVariables.cost_passenger_boarding_night = 0;
                        MyVariables.cost_passenger_pre_boarding_day = 0;
                        MyVariables.cost_passenger_pre_boarding_night = 0;
                    } else { //если результат не нулевой
                        //разбираем строку RESULT
                        JSONObject joRESULT = new JSONObject(jo.getString(RESULT));
                        MyVariables.cost_km_city = Float.parseFloat(joRESULT.getString("cost_km_city"));
                        MyVariables.cost_km_intercity = Float.parseFloat(joRESULT.getString("cost_km_intercity"));
                        MyVariables.cost_km_suburb = Float.parseFloat(joRESULT.getString("cost_km_suburb"));
                        MyVariables.cost_km_n1 = Float.parseFloat(joRESULT.getString("cost_km_n1"));
                        MyVariables.cost_stopping = Float.parseFloat(joRESULT.getString("cost_stopping"));
                        MyVariables.cost_passenger_boarding_day = Float.parseFloat(joRESULT.getString("cost_passenger_boarding_day"));
                        MyVariables.cost_passenger_boarding_night = Float.parseFloat(joRESULT.getString("cost_passenger_boarding_night"));
                        MyVariables.cost_passenger_pre_boarding_day = Float.parseFloat(joRESULT.getString("cost_passenger_pre_boarding_day"));
                        MyVariables.cost_passenger_pre_boarding_night = Float.parseFloat(joRESULT.getString("cost_passenger_pre_boarding_night"));
                        Log.d(TAG, "Обработались настройки таксометра");
                        Log.d(TAG, String.valueOf(MyVariables.cost_km_city + " руб/км") + String.valueOf(MyVariables.cost_km_intercity + " руб/км"));
                    }
                } else { //если есть ошибки
                    MyVariables.cost_km_city = 0;
                    MyVariables.cost_km_intercity = 0;
                    MyVariables.cost_km_suburb = 0;
                    MyVariables.cost_km_n1 = 0;
                    MyVariables.cost_stopping = 0;
                    MyVariables.cost_passenger_boarding_day = 0;
                    MyVariables.cost_passenger_boarding_night = 0;
                    MyVariables.cost_passenger_pre_boarding_day = 0;
                    MyVariables.cost_passenger_pre_boarding_night = 0;
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "Ошибка обработки JSON");
                e.printStackTrace();
            }
        } catch (IOException e) {
            MyVariables.InOuExcept = true;
            e.printStackTrace();
        }
    }

    // check network connection
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    /*
    @SuppressLint("NewApi")
    @Override
    protected void onRestart() { //
        super.onRestart();
        if (MyVariables.indexArrList>=0) {
            Log.d(TAG, "MyVariables.indexArrList = "+ MyVariables.indexArrList);
            Log.d(TAG, "zakaz.size = "+ zakaz.size() + " значение "+zakaz.get(0));
            Log.d(TAG, "telefon.size = "+ telefon.size() + " значение "+telefon.get(0));
            Log.d(TAG, "kode.size = "+ kode.size() + " значение "+kode.get(0));
            Log.d(TAG, "dat.size = "+ dat.size() + " значение "+dat.get(0));
            Log.d(TAG, "tim.size = "+ tim.size() + " значение "+tim.get(0));
            Log.d(TAG, "adres.size = "+ adres.size() + " значение "+adres.get(0));
            Log.d(TAG, "car.size = "+ car.size() + " значение "+car.get(0));
            Log.d(TAG, "predvar.size = "+ predvar.size() + " значение "+predvar.get(0));
            zakaz.remove(0);
            telefon.remove(0);
            kode.remove(0);
            dat.remove(0);
            tim.remove(0);
            adres.remove(0);
          //  car.remove(0);
          // predvar.remove(0);

            MyVariables.indexArrList = -1;
    }
    }  */

    @SuppressLint("NewApi")
    @Override
    protected void onResume() { //запускаем таксометр и определитель координат когда активити видно
        super.onResume();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //если GPS включено

            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result != PackageManager.PERMISSION_GRANTED && result2 != PackageManager.PERMISSION_GRANTED) return;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 0, locationListener);
        } else Toast.makeText(this, "GPS not enabled!", Toast.LENGTH_SHORT).show();


        //конец получения настроек с сервера
        FlagAppStarted = true;

        //когда закрываем Activity таксометра надо быстрей обновить список заказов
        Log.d(TAG, "******************* сработал OnResume   *****************************");
        if (GettingZak == false) { //если в данный момент не идет получение заказа
            new GetAsincTask().execute(httpPath);

            if (ZakazEmpty == false) {
                updateUsersList(); //обновляем ListView
                PlyNewZak();
            } else {
                populateUsersList();
            }
            Log.d(TAG, "******************* отправили GetAsincTask   *****************************");
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStop() {  //запускаем таксометр и определитель координат когда активити не видно
        super.onStop();
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //если GPS включено

            int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result != PackageManager.PERMISSION_GRANTED && result2 != PackageManager.PERMISSION_GRANTED) return;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 0, locationListener);
        } else Toast.makeText(this, "GPS not enabled!", Toast.LENGTH_SHORT).show();


        //конец получения настроек с сервера
        //FlagAppStarted = true;
    }


    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        super.onPause();
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
            int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (result != PackageManager.PERMISSION_GRANTED && result2 != PackageManager.PERMISSION_GRANTED) return;
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            //проверка доступен ли интернет
            if(isConnected()) iconINETyes();
            else iconINETno();
            //проверка включен ли GPS
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                iconGPSyes();
               // Toast.makeText(getApplicationContext(), MyVariables.Lat + "  " + MyVariables.Lon + "  " + location.getSpeed(), Toast.LENGTH_SHORT).show();
               showLocation(location);
            }
            else iconGPSno();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void showLocation(Location location) {
        //  this.mlocation = location;

        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Lat=formatLat(location);
            Lat = Lat.replace(",", ".");
            MyVariables.Lat=Lat;
            Lon=formatLon(location);
            Lon=Lon.replace(',', '.');
            MyVariables.Lon=Lon;
            //Fragment6.TaxometrMain[12]="Сработала showLocation";
            if (StartTax) {
                showTaxometr(location);
            }
            // etResponse.setText("Your coordinates:" + " Lat=" + Lat + " ,Lon=" + Lon + PswCorrect);
            // new HttpAsyncTaskPost().execute(MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/setcoord");
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Lat=formatLat(location);
            Lat = Lat.replace(",", ".");
            MyVariables.Lat=Lat;
            Lon=formatLon(location);
            Lon=Lon.replace(',', '.');
            MyVariables.Lon=Lon;
            // etResponse.setText("Your coordinates:" + " Lat=" + Lat + " ,Lon=" + Lon + PswCorrect);
            // new HttpAsyncTaskPost().execute(MyVariables.HTTPAdress+MyVariables.SAVED_TEXT_1+"/"+MyVariables.SAVED_TEXT_2+"/setcoord");
        }
        //if (StartTax==true) {  myIntent.putExtra("btnStr","Proba");  }
        //ActivityThree.buttonStart.setText("Проба");
       // Toast.makeText(getApplicationContext(), "StartTax=" +StartTax + "  " + location.getSpeed()  + "  " +  NewSpeed   + "  " +  Itogo, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("DefaultLocale") private String formatLat(Location location) {
        if (location == null)
            return "";
        return String.format("%1$.10f",location.getLatitude());
    }

    @SuppressLint("DefaultLocale") private String formatLon(Location location) {
        if (location == null)
            return "";
        return String.format("%1$.10f",location.getLongitude());
    }

    @SuppressLint("DefaultLocale")  private void showTaxometr(Location location) {
        // if (Fragment6.StartTax) {
        if (FirstTime) {
            OldTime=NewTime;
            NewTime= SystemClock.uptimeMillis();
            TimeInterval=0;
            TimeItogo = 0;
            NewSpeed = 0;
        }
        else {
            OldTime=NewTime;
            NewTime=SystemClock.uptimeMillis();
            TimeInterval=NewTime-OldTime;
            TimeItogo = TimeItogo + TimeInterval;
        }
        OldSpeed=NewSpeed;
        NewSpeed=location.getSpeed();
        AverageSpeed=(NewSpeed+OldSpeed)/2;
        Distance=AverageSpeed*TimeInterval/1000; //  м/с * сек

      		/*Определяем в каком месте находимся*/
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.873696, 40.084303, 45.884451, 40.130909, 45.845751, 40.168203, 45.839697, 40.130051)) tutGorod=true;
        else tutGorod=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.869187, 40.083370, 45.869277, 40.091824, 45.862434, 40.100364, 45.862135, 40.082426)) tutVG=true;
        else tutVG=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.854390, 40.060062, 45.890246, 40.130443, 45.836334, 40.180654, 45.810375, 40.144519)) tutPG=true;
        else tutPG=false;
        tutGorod=tutGorod|tutVG; //хоть город хоть военный считаем что город
        //дальше будем искать где мы - в районе или в межгороде
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 46.1089, 40.0307, 45.9595, 40.5001, 45.6071, 40.5629, 45.5917, 40.0397)) tutRn=true; //тут район, большой квадрат
        else tutRn=false;
        if (intersect(Double.parseDouble(MyVariables.Lon), Double.parseDouble(MyVariables.Lat), 45.8831, 39.8162, 45.9905, 40.0537, 45.8125, 40.1073, 45.8226, 39.8454)) tutRn2=true; //тут район, малый квадрат Новорождественская
        else tutRn2=false;
        tutRn=tutRn|tutRn2; //хоть большой квадрат хоть малый считаем что это район

      		/* В зависимости от места выбираем тариф */
        if (PauseTax==false ) { //если кнопка Старт не зеленая сумма не изменяется
            if (AverageSpeed!=0) { //если скорость не равна нулю
                if (tutRn==false) { //если это межгород
                    //это межгород
                    ItogKmMg=ItogKmMg+Distance;
                    Cost = Distance*MyVariables.cost_km_n1/1000;
                }
                else { //если это не межгород
                    if ((tutPG==false) & (tutGorod==false)) {  //проверяем может это район
                        //значит это район
                        ItogKmRn=ItogKmRn+Distance;
                        Cost = Distance*MyVariables.cost_km_intercity/1000;
                    }
                    if ((tutPG==true) & (tutGorod==false)) { //если это пригород
                        //значит это пригород
                        ItogKmPrig=ItogKmPrig+Distance;
                        Cost = Distance*MyVariables.cost_km_suburb/1000;
                    }
                    if ((tutPG==true) & (tutGorod==true)) { //если это город
                        //значит это город
                        ItogKmGorod=ItogKmGorod+Distance;
                        Cost = Distance*MyVariables.cost_km_city/1000;
                    }
                }
            }
            else {
                Cost = (MyVariables.cost_stopping / 60) * TimeInterval / 1000; //если красный светофор- новая стоимость равна 5 руб/мин 5/60 получ сек умнож на TimeInterval/1000 сек
                TimeStay=TimeStay + TimeInterval;
            }
            Itogo=Itogo+Cost;
            MyVariables.Itogo = Itogo;
        }

        //отображаем результат вычислений
        hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(TimeStay),
                TimeUnit.MILLISECONDS.toMinutes(TimeStay) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(TimeStay) % TimeUnit.MINUTES.toSeconds(1));
      //  if (hms.contains("null")) hms="0 c";
        //String strItogo = String.format("%.2f",Itogo);
     /*   Fragment2.TaxometrMain[0]="Сумма: " + String.format("%.2f",Itogo) + " руб";
        Fragment2.TaxometrMain[1]="Время в пути: "+hms;
        Fragment2.TaxometrMain[2]="Дистанция город: " + String.format("%.1f",ItogKmGorod) +" м"; //String.format("%1$.1f",
        Fragment2.TaxometrMain[3]="Дистанция пригород: " + String.format("%.1f",ItogKmPrig) +" м";
        Fragment2.TaxometrMain[4]="Дистанция район: "+String.format("%.1f",ItogKmRn)+" м";
        Fragment2.TaxometrMain[5]="Дистанция межгород: "+String.format("%.1f",ItogKmMg)+" м";
        Fragment2.TaxometrMain[6]="Тариф "+String.valueOf(MyVariables.cost_km_city)+" руб/км город";
        Fragment2.TaxometrMain[7]="Тариф "+String.valueOf(MyVariables.cost_km_suburb)+" руб/км пригород";
        Fragment2.TaxometrMain[8]="Тариф "+String.valueOf(MyVariables.cost_km_intercity)+" руб/км район";
        Fragment2.TaxometrMain[9]="Тариф "+String.valueOf(MyVariables.cost_km_n1)+" руб/км межгород";
        Fragment2.TaxometrMain[10]="Тариф "+String.valueOf(MyVariables.cost_stopping)+" руб/мин стоянка";
        Fragment2.TaxometrMain[11]="Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_day)+" руб посадка день";
        Fragment2.TaxometrMain[12]="Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_night)+" руб посадка ночь";
        Fragment2.TaxometrMain[13]="Тариф "+String.valueOf(MyVariables.cost_passenger_pre_boarding_day)+" руб предвар день";
        Fragment2.adapter.notifyDataSetChanged();*/
        /*ActivityThree.buttonStart.setText(String.format("%.2f",Itogo) + " руб");
        ActivityThree.tvDistance.setText("Дистанция город: " + String.format("%.1f",ItogKmGorod) +" м");
        ActivityThree.tvStay.setText("Время в пути: "+hms);*/


        FirstTime=false;

    }

    public static boolean intersect(double XLon, double YLat, double y1, double x1, double y2, double x2, double y3, double x3, double y4, double x4) {
        //Определяется находится ли точка внутри четырехугольника, в городе или нет

        boolean E214=false;
        boolean E243=false;
        boolean E143=false;
        boolean E123=false;
        boolean E423=false;
        boolean E412=false;
        boolean E312=false;
        boolean E314=false;

        //Пересечение отрезка E-2 с отрезком 1-4
        d=(XLon-x2)*(y4-y1)-(YLat-y2)*(x4-x1);
        da=(XLon-x1)*(y4-y1)-(YLat-y1)*(x4-x1);
        db=(XLon-x2)*(YLat-y1)-(YLat-y2)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x2-XLon);
                YIntersect=YLat+ta*(y2-YLat);
                E214=false;
            }
            else E214=true;
        }
        //Пересечение отрезка E-2 с отрезком 4-3
        d=(XLon-x2)*(y4-y3)-(YLat-y2)*(x4-x3);
        da=(XLon-x3)*(y4-y3)-(YLat-y3)*(x4-x3);
        db=(XLon-x2)*(YLat-y3)-(YLat-y2)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x2-XLon);
                YIntersect=YLat+ta*(y2-YLat);
                E243=false;
            }
            else E243=true;
        }
        //Пересечение отрезка E-1 с отрезком 4-3
        d=(XLon-x1)*(y4-y3)-(YLat-y1)*(x4-x3);
        da=(XLon-x3)*(y4-y3)-(YLat-y3)*(x4-x3);
        db=(XLon-x1)*(YLat-y3)-(YLat-y1)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x1-XLon);
                YIntersect=YLat+ta*(y1-YLat);
                E143=false;
            }
            else E143=true;
        }
        //Пересечение отрезка E-1 с отрезком 2-3
        d=(XLon-x1)*(y2-y3)-(YLat-y1)*(x2-x3);
        da=(XLon-x3)*(y2-y3)-(YLat-y3)*(x2-x3);
        db=(XLon-x1)*(YLat-y3)-(YLat-y1)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x1-XLon);
                YIntersect=YLat+ta*(y1-YLat);
                E123=false;
            }
            else E123=true;
        }
        //Пересечение отрезка E-4 с отрезком 2-3
        d=(XLon-x4)*(y2-y3)-(YLat-y4)*(x2-x3);
        da=(XLon-x3)*(y2-y3)-(YLat-y3)*(x2-x3);
        db=(XLon-x4)*(YLat-y3)-(YLat-y4)*(XLon-x3);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x4-XLon);
                YIntersect=YLat+ta*(y4-YLat);
                E423=false;
            }
            else E423=true;
        }
        //Пересечение отрезка E-4 с отрезком 1-2////////
        d=(XLon-x4)*(y2-y1)-(YLat-y4)*(x2-x1);
        da=(XLon-x1)*(y2-y1)-(YLat-y1)*(x2-x1);
        db=(XLon-x4)*(YLat-y1)-(YLat-y4)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x4-XLon);
                YIntersect=YLat+ta*(y4-YLat);
                E412=false;
            }
            else E412=true;
        }
        //Пересечение отрезка E-3 с отрезком 1-4
        d=(XLon-x3)*(y4-y1)-(YLat-y3)*(x4-x1);
        da=(XLon-x1)*(y4-y1)-(YLat-y1)*(x4-x1);
        db=(XLon-x3)*(YLat-y1)-(YLat-y3)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x3-XLon);
                YIntersect=YLat+ta*(y3-YLat);
                E314=false;
            }
            else E314=true;
        }
        //Пересечение отрезка E-3 с отрезком 1-2
        d=(XLon-x3)*(y2-y1)-(YLat-y3)*(x2-x1);
        da=(XLon-x1)*(y2-y1)-(YLat-y1)*(x2-x1);
        db=(XLon-x3)*(YLat-y1)-(YLat-y3)*(XLon-x1);
        if (d!=0) {
            ta=da/d;
            tb=db/d;
            if ((ta>0 & ta<1) & (tb>0 & tb<1)) {
                XIntersect=XLon+ta*(x3-XLon);
                YIntersect=YLat+ta*(y3-YLat);
                E312=false;
            }
            else E312=true;
        }
        if ((E214&E243)&(E143&E123)&(E423&E412)&(E312&E314)) return true;
        else return false;

        //закончили определение четырехугольника мы в городе
    }
    public void iconINETyes() { //собственно функция смены иконки
        if (FlagAppStarted)	 item3.setIcon(R.drawable.ic_action_globe);
    }

    public void iconINETno() { //собственно функция смены иконки
        if (FlagAppStarted)  item3.setIcon(R.drawable.ic_action_globe_2);
    }

    public void iconGPSyes() { //собственно функция смены иконки
        if (FlagAppStarted)  item2.setIcon(R.drawable.ic_action_location_2);
    }

    public void iconGPSno() { //собственно функция смены иконки
        if (FlagAppStarted)  item2.setIcon(R.drawable.ic_action_location_3);
    }

     public void PlyNewZak(){
      /*   boolean flagNewZak = false;
         boolean flagsred = false;
         boolean flagPlyMus = false;
         for (int i=0; i<zakaz.size(); i++) {

             for (int k=0; k<plyzak.size(); k++) {
                 if (plyzak.get(k)==zakaz.get(i)) flagsred = true;
                 else flagsred =false;

                     Log.d(TAG, "plyzak="+Integer.toString(plyzak.get(k)) +"  " + "zakaz="+Integer.toString(zakaz.get(i)) +"  flagsred=" + flagsred);
                flagNewZak=flagNewZak|flagsred;
                 Log.d(TAG, "flagNewZak=" + flagNewZak);
             }
             if (flagNewZak==false) flagPlyMus=true;
             Log.d(TAG, "------------flagNewZak=" + flagNewZak);
             flagNewZak=false;
         }*/
         boolean flagsred2 = false;
         boolean flagsred = false;
         boolean flagPlyMus = false;

         for (int i=0; i<zakaz.size(); i++) {

             for (int k=0; k<plyzak.size(); k++) {
                 if (plyzak.get(k) - zakaz.get(i)==0) flagsred = true;
                 else flagsred =false;

                 Log.d(TAG, "plyzak="+Integer.toString(plyzak.get(k)) +"  " + "zakaz="+Integer.toString(zakaz.get(i)) +"  flagsred=" + flagsred);
                 flagsred2=flagsred2|flagsred;
                 Log.d(TAG, "flagsred2=" + flagsred2);
             }
             if (flagsred2==false & ZakazEmpty==false) flagPlyMus=true;
             Log.d(TAG, "------------flagsred2=" + flagsred2);
             flagsred2=false;
         }
         Log.d(TAG, "------------flagPlyMus=" + flagPlyMus);
         flagMusYes=flagPlyMus;

         Log.d(TAG, "------------flagMusYes=" + flagMusYes);
         if (flagMusYes) {
             mediaPlayer = MediaPlayer.create(this, R.raw.sound_5);
             mediaPlayer.start();
         }
     }

    public class  LonLatAsincTask extends AsyncTask<String, Void, Void> {
        String response = "";

        @Override
        protected Void doInBackground(String... params) {
            Log.d("LonLat", "*******************    POST Lon, Lat   *****************************");
            String url = params[0];
            JSONObject jsonBody;
            String requestBody;
            HttpURLConnection urlConnection = null;
            try {
                jsonBody = new JSONObject();
                jsonBody.put("lon", MyVariables.Lon);
                jsonBody.put("lat", MyVariables.Lat);
                requestBody = jsonBody.toString();
                urlConnection = (HttpURLConnection) Utils.makeRequest("POST", url, null, "application/json", requestBody);
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response+=line;
                    }
                    MyVariables.InOuExcept = false;
                    Log.d("LonLat", response);
                    try {
                        JSONObject jo =  new JSONObject(response);
                        errPost= jo.getString("error");
                        Log.d("LonLat", String.valueOf(errPost));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Log.d(TAG, String.valueOf(errPost));
                } else {
                    // Toast.makeText(getApplicationContext(), "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                    Log.d("LonLat", String.valueOf(urlConnection.getResponseCode()));
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
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //получили JSON строку с сервера
            if  (MyVariables.InOuExcept) {
                Toast.makeText(getApplicationContext(), "Ошибка соединения с сервером!", Toast.LENGTH_SHORT).show();} else {
                if (errPost.contains("none")) {
                   // Toast.makeText(getApplicationContext(), "Сервер ответил ОК", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), ActivityTwo.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Сервер ответил Ошибка", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
