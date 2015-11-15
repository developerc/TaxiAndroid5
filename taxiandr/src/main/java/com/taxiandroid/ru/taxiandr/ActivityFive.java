package com.taxiandroid.ru.taxiandr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityFive extends AppCompatActivity {

  /*  static String[] TaxometrMain = { "Тариф 0 руб/км город", "Тариф 0 руб/км пригород", "Тариф 0 руб/км район", "Тариф 0 руб/км межгород", "Тариф 0 руб/мин стоянка",
            "Тариф 0 руб посадка день","Тариф 0 руб посадка ночь","Тариф 0 руб предвар день","Тариф 0 руб предвар ночь"};
    static ListView lvMain;
    static ArrayAdapter<String> adapter;*/
  private ArrayList<HashMap<String, Object>> mTarList;
    private static final String Tarif = "Тариф"; // Левый текст
    private static final String Cost = "Стоимость"; // стоимость

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five);

        /*lvMain = (ListView) this.findViewById(R.id.lvMain);
        // adapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1, TaxometrMain);
        TaxometrMain[0]= "Тариф " + String.valueOf(MyVariables.cost_km_city) + " руб/км город";
        TaxometrMain[1] = "Тариф "+String.valueOf(MyVariables.cost_km_suburb)+" руб/км пригород";
        TaxometrMain[2] = "Тариф "+String.valueOf(MyVariables.cost_km_intercity)+" руб/км район";
        TaxometrMain[3] = "Тариф "+String.valueOf(MyVariables.cost_km_n1)+" руб/км межгород";
        TaxometrMain[4] = "Тариф "+String.valueOf(MyVariables.cost_stopping)+" руб/мин стоянка";
        TaxometrMain[5] = "Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_day)+" руб посадка день";
        TaxometrMain[6] = "Тариф "+String.valueOf(MyVariables.cost_passenger_boarding_night)+" руб посадка ночь";
        TaxometrMain[7] = "Тариф "+String.valueOf(MyVariables.cost_passenger_pre_boarding_day)+" руб предвар день";
        TaxometrMain[8] = "Тариф "+String.valueOf(MyVariables.cost_passenger_pre_boarding_night)+" руб предвар ночь";
        adapter = new ArrayAdapter<String>(this,R.layout.list_item, TaxometrMain);
        lvMain.setAdapter(adapter);*/
        ListView lvMain = (ListView) this.findViewById(R.id.lvMain);
        // создаем массив списков
        mTarList = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;

        hm = new HashMap<>();
       hm.put(Tarif, "Тариф");
        hm.put(Cost, "Стоимость");
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "город, руб/км");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_km_city));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "пригород, руб/км");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_km_suburb));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "район, руб/км");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_km_intercity));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "межгород, руб/км");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_km_n1));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "стоянка, руб/мин");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_stopping));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "посадка день, руб");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_passenger_boarding_day));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "посадка ночь, руб");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_passenger_boarding_night));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "предварительная посадка день, руб");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_passenger_pre_boarding_day));
        mTarList.add(hm);

        hm = new HashMap<>();
        hm.put(Tarif, "предварительная посадка ночь, руб");
        hm.put(Cost, String.format("%.0f", MyVariables.cost_passenger_pre_boarding_night));
        mTarList.add(hm);

        SimpleAdapter adapter = new SimpleAdapter(this, mTarList, R.layout.list_item, new String[]{Tarif, Cost}, new int[]{R.id.tvName, R.id.tvCost});
        lvMain.setAdapter(adapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_five, menu);
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
