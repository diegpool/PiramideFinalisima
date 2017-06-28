package com.example.diego.piramidefinalisima;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Instrucciones extends AppCompatActivity {

    private MediaPlayer cancion_instrucciones;
    /*** Sobrecarga de operador onBackPress() para retornar al menu **/
    @Override
    public void onBackPressed() {
        finish();
        cancion_instrucciones.stop();
        Intent pasomenu = new Intent(this, Menu.class);
        startActivity(pasomenu);
    }
    @Override protected void onPause() {
        super.onPause();
        cancion_instrucciones.stop();
        finish();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instrucciones);
        final Intent pasomenu = new Intent(Instrucciones.this, Menu.class);
        cancion_instrucciones = MediaPlayer.create(this,R.raw.cancionintru);
        Button boton_volver = (Button) findViewById(R.id.botonvolver);
        //getSupportActionBar().hide();
        cancion_instrucciones.setLooping(true);
        cancion_instrucciones.start();
        boton_volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                cancion_instrucciones.stop();
                startActivity(pasomenu);
            }
        });
    }
}
