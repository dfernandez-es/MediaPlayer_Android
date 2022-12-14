package com.example.dff50.act_3_mediaplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;
    File rutaArchivo;
    Handler h = new Handler();
    SeekBar barra;

    ImageView foto;
    TextView anyo;
    TextView grupo;
    TextView posicion,duracion,titulo;
    double tiempoFin,tiempoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        barra=(SeekBar)findViewById(R.id.barra);
        titulo=(TextView)findViewById(R.id.titulo);
        posicion=(TextView)findViewById(R.id.posicion);
        duracion=(TextView)findViewById(R.id.duracion);
        foto=(ImageView)findViewById(R.id.foto);
        anyo=(TextView)findViewById(R.id.anyo);
        grupo=(TextView)findViewById(R.id.grupo);

        //Se solicita permisos de escritura en la memoria externa, nos permitira leer el archivo
        //y poder crear la carpeta que se solicita en la practica
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        rutaArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        //Comprueba si la carpeta que se solicita en la practica existe, en caso negativo la crea
        File carpeta = new File(rutaArchivo+"/MiMusica");
        if(!carpeta.exists()) carpeta.mkdirs();

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

    @SuppressLint("DefaultLocale")
    private void CargarMediaPlayer() {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }


    @SuppressLint("DefaultLocale")
    private void CargarCancion(String fichero) {
        try {
            mp.setDataSource(fichero);
            mp.prepare();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al leer fichero o no seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }
        barra.setMax(mp.getDuration());
        barra.setProgress((int)tiempoActual);
        //Cortamos la cadena para quedarnos con el nombre del archivo
        titulo.setText(fichero.substring(fichero.lastIndexOf("/")+1));
        tiempoFin=mp.getDuration();
        tiempoActual=mp.getCurrentPosition();

        //Comprueba si el archivo de audio contiene una imagen y la muestra
        MediaMetadataRetriever imagenMP3 = new MediaMetadataRetriever();
        imagenMP3.setDataSource(fichero);
        byte[] artBytes =  imagenMP3.getEmbeddedPicture();
        if(artBytes!=null)
        {
            Bitmap bm = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
           foto.setImageBitmap(bm);
        }
        else
        {
           foto.setImageResource(R.drawable.ic_menu_camera);
        }

        //Se comprueba si el archivo contiene metadatos del grupo y los muestra
        if(imagenMP3.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) != null){
            grupo.setText(imagenMP3.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        }
        else{
            grupo.setText("");
        }

        //Se comprueba si el archivo contiene metadatos de la fecha de publicacion y los muestra
        if(imagenMP3.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR) != null){
            anyo.setText(imagenMP3.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
        }
        else{
            anyo.setText("");
        }

        duracion.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) tiempoFin),
                TimeUnit.MILLISECONDS.toSeconds((long) tiempoFin) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                        tiempoFin)))
        );
        posicion.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) tiempoActual),
                TimeUnit.MILLISECONDS.toSeconds((long) tiempoActual) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                        tiempoActual)))
        );

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

        if(id == R.id.abrirFichero){
            if(mp.isPlaying()) {
                mp.pause();
            }
            Intent i = new Intent(getApplicationContext(),ListarCarpeta.class);
            startActivityForResult(i,1);
        }

        return super.onOptionsItemSelected(item);
    }

    //Se encarga de actualizar la barra de progreso segun se ejecuta la cancion
    public Runnable RefrescarBarra = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            //Solo se ejecuta si la musica esta en ejecucion
            if(mp.isPlaying()) {
                barra.setProgress((mp.getCurrentPosition()));
                tiempoActual = mp.getCurrentPosition();
                posicion.setText(String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) tiempoActual),
                        TimeUnit.MILLISECONDS.toSeconds((long) tiempoActual) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                tiempoActual)))
                );
                //Se llama a si misma cada 100 milisegundos
                h.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK) {
                String cancionSeleccionada = data.getExtras().getString("cancionSelec");
                if(cancionSeleccionada != null){
                    mp.reset();
                    tiempoActual=0;
                    CargarCancion(cancionSeleccionada);
                }
            }
        }
    }
}
