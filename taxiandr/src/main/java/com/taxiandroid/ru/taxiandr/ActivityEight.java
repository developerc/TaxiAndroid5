package com.taxiandroid.ru.taxiandr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityEight extends AppCompatActivity {
    ArrayList<StayArea> stayAreas;
    ListView lvEigth;
    CustomEigthAdapter adapter;
    String ClkStay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);
        populateEigthList();
        lvEigth.setOnItemClickListener(itemClickListener);
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
            Toast.makeText(getApplicationContext(),
                    "Вы выбрали " + ClkStay, Toast.LENGTH_SHORT).show();
        }
    };
}
