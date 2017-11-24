package com.example.dff50.act_3_mediaplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;
    File rutaArchivo;
    String cancion = "master.mp3";
    Handler h = new Handler();
    SeekBar barra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        barra=(SeekBar)findViewById(R.id.barra);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Se solicita permisos de lectura en la memoria externa
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        //Funciones que se ejecutan al pulsar sobre la barra
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int prograso, boolean esUsuario) {
                if(esUsuario){
                    mp.seekTo(prograso);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mp.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.start();
            }
        });
    }

    //Se utiliza para comprobar si se tiene autorizacion para acceder a los permisos solicitados
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CargarMediaPlayer();
            }
        }
    }

    private void CargarMediaPlayer() {
        mp = new MediaPlayer();
        String fichero = rutaArchivo + "/" + cancion;
        try {
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setDataSource(fichero);
            mp.prepare();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al leer el archivo", Toast.LENGTH_SHORT).show();
            return;
        }
        barra.setMax(mp.getDuration());
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(getApplicationContext(), "Cancion finalizada", Toast.LENGTH_SHORT).show();
                mp.seekTo(0);
                barra.setProgress(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.play) {
            mp.start();
            h.postDelayed(RefrescarBarra,100);
        }

        if (id == R.id.stop) {
            mp.pause();
        }


        return super.onOptionsItemSelected(item);
    }

    //Se encarga de actualizar la barra de progreso segun se ejecuta la cancion
    public Runnable RefrescarBarra = new Runnable() {
        @Override
        public void run() {
            //Solo se ejecuta si la musica esta en ejecucion
            if(mp.isPlaying()) {
                barra.setProgress((mp.getCurrentPosition()));
                //Se llama a si misma cada 100 milisegundos
                h.postDelayed(this, 100);
            }
        }
    };

}
