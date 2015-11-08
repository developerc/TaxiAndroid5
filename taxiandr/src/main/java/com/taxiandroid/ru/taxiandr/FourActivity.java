package com.taxiandroid.ru.taxiandr;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FourActivity extends AppCompatActivity {
    Button btnSave, btnLoad;
    EditText etLgn, etPsw;
    SharedPreferences sPref;

    final String SAVED_TEXT_LGN = "saved_text_lgn";
    final String SAVED_TEXT_PSW = "saved_text_psw";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        etLgn = (EditText) this.findViewById(R.id.etLgn);
        etPsw = (EditText) this.findViewById(R.id.etPsw);

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

        sPref = this.getSharedPreferences("pref",0);
        MyVariables.SAVED_TEXT_1 = sPref.getString(SAVED_TEXT_LGN, "");
        MyVariables.SAVED_TEXT_2 = sPref.getString(SAVED_TEXT_PSW, "");

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
    }

    private void loadText() {
        sPref = this.getSharedPreferences("pref",0);
        String savedTextLgn = sPref.getString(SAVED_TEXT_LGN, "");
        String savedTextPsw = sPref.getString(SAVED_TEXT_PSW, "");

        etLgn.setText(savedTextLgn);
        etPsw.setText(savedTextPsw);
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
}
