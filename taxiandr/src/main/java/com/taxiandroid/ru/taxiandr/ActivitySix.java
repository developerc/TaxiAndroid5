package com.taxiandroid.ru.taxiandr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class ActivitySix extends AppCompatActivity implements View.OnClickListener {
    Button btnBack, btnFst10, btnClear;
    private static DatabaseHelper mDatabaseHelper;
    private static final String TAG = "myLogs";
    List<Taximeter> taximeterList;
    static int CountD=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        mDatabaseHelper = new DatabaseHelper(this);

        // найдем View-элементы
        btnBack = (Button) findViewById(R.id.btnBack);
        btnFst10 = (Button) findViewById(R.id.btnFst10);
        btnClear = (Button) findViewById(R.id.btnClear);

        // присваиваем обработчик кнопкам
        btnBack.setOnClickListener(this);
        btnFst10.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
// по id определеяем кнопку, вызвавшую этот обработчик
        switch (v.getId()) {
            case R.id.btnBack:
                finish();  //можем закрыть активити до состояния onDestroy
                break;
            case R.id.btnFst10:
                // Ищем все записи в таблице
                fndFst10();

                break;
            case R.id.btnClear:
                //
              //  mDatabaseHelper.deletePoezdki();
                mDatabaseHelper.upgradeTaximeter();
                fndFst10();
                break;
        }
    }

    private  void fndFst10() {
        // TODO Auto-generated method stub
        if (mDatabaseHelper.getTaximeterCount() > 0) {
            // Считываем все контакты в ListView
            List<Taximeter> contacts = mDatabaseHelper.getAllTaximeter();

            ArrayAdapter<Taximeter> adapter = new ArrayAdapter<Taximeter>(this, android.R.layout.simple_list_item_1,
                    android.R.id.text1, contacts);
            ListView catsListView = (ListView) findViewById(R.id.lvMain);
            catsListView.setAdapter(adapter);
        }
        else Toast.makeText(this, "Нет поездок!", Toast.LENGTH_SHORT).show();
    }
}
