package com.example.diego.piramidefinalisima;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Ganar extends AppCompatActivity {
    private MediaPlayer cancion_victoria;
    @Override
    public void onBackPressed() {
        finish();
        cancion_victoria.stop();
        Intent pasomenu = new Intent(this, Menu.class);
        startActivity(pasomenu);
    }
    @Override protected void onPause() {
        super.onPause();
        cancion_victoria.stop();
        finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ganar);
        final Intent pasomain = new Intent(Ganar.this, MainActivity.class);

        cancion_victoria = MediaPlayer.create(this,R.raw.sonidovictoria);
        Button boton_reiniciar = (Button) findViewById(R.id.botonreiniciar);
        Button boton_salir = (Button) findViewById(R.id.botonsalirganar);

        cancion_victoria.start();
        boton_reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancion_victoria.stop();
                finish();
                startActivity(pasomain);
            }
        });



        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


}

