package com.caiobraga.mapapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        inserirCidadesIniciais();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        listView = findViewById(R.id.listView);
        String[] itens = {"Colatina", "viçosa", "DPI", "Fechar aplicação"};

        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itens);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) listView.getItemAtPosition(position);
                if(selectedItem == "Fechar aplicação"){
                    finish();
                }

                Intent it = new Intent(MainActivity.this, Mapactivity.class);
                it.putExtra("cidade", selectedItem);
                startActivity(it);
                Toast.makeText(MainActivity.this, "Clicado: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Context getAppContext() {
        return context;
    }

    private void inserirCidadesIniciais() {
        BancoDadosSingleton db = BancoDadosSingleton.getInstance();

        if (db.buscar("Location", null, "", "").getCount() == 0) {
            String[][] cidades = {
                    {"Viçosa", "-20.76313446588187", "-42.86664841063025"},
                    {"Colatina", "-19.54061360351423", "-40.63590846745496"},
                    {"DPI", "-20.765134339893592", "-42.86846994734335"}
            };

            for (String[] cidade : cidades) {
                ContentValues values = new ContentValues();
                values.put("descricao", cidade[0]);
                values.put("latitude", cidade[1]);
                values.put("longitude", cidade[2]);
                db.inserir("Location", values);
            }
        }
    }

}