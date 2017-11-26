package com.example.dff50.act_3_mediaplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class ListarCarpeta extends AppCompatActivity {
    ListView listaArchivos;
    ArrayList<String> ficheros;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_carpeta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Archivos de la carpeta MiMusica");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        listaArchivos=(ListView)findViewById(R.id.lv_lista);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ficheros = Escanear();
                adaptador = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        ficheros);
                listaArchivos.setAdapter(adaptador);

                //Definimos funcion a ejecutar al seleccionar un elemento del listview
                listaArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Devolvemos al layaout principal la cancion seleccionada
                        Intent x=new Intent();
                        x.putExtra("cancionSelec",ficheros.get(position));
                        setResult(RESULT_OK,x);
                        finish();
                    }
                });

            }

        }
    }


    public ArrayList<String> Escanear(){
        ArrayList<String> paths = new ArrayList<String>();
        File ruta= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/MiMusica");
        File[] files = ruta.listFiles();
        for (int i = 0; i < files.length; ++i) {
            paths.add(files[i].getAbsolutePath());
        }
        return paths;
    }

    //Funcion que permite volver atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
