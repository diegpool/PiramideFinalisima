package com.example.diego.piramidefinalisima;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class Menu extends AppCompatActivity {

    String mazocargado, matrizcargada, mazooriginal, pozocargado, jugadascargada, mazo2cargado;
    int NumeroJugadas, tiempobase;
    private MediaPlayer cancion_menu;

    @Override
    public void onBackPressed() {
        cancion_menu.stop();
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        final Intent pasomain = new Intent(Menu.this, MainActivity.class);
        final Intent pasoinstrucciones = new Intent(Menu.this, Instrucciones.class);
        cancion_menu = MediaPlayer.create(this, R.raw.cancionmenu);
        Button boton_iniciar = (Button) findViewById(R.id.botoniniciar);
        Button boton_instrucciones = (Button) findViewById(R.id.botoninstrucciones);
        Button boton_salir = (Button) findViewById(R.id.botonsalir);
        Button boton_continuar = (Button) findViewById(R.id.botoncontinuar);
        try {
            cargarpartida();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final boolean pg;
        pg = jugadascargada != null;
        cancion_menu.setLooping(true);
        cancion_menu.start();

        //getSupportActionBar().hide();
        boton_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                builder.setMessage("¿Que color de carta prefieres?")
                        .setTitle("Selecciona")
                        .setCancelable(false)
                        .setNegativeButton("Rojo",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        cancion_menu.stop();
                                        pasomain.putExtra("PartidaGuardada",false);
                                        pasomain.putExtra("Carta","cartar");
                                        startActivity(pasomain);
                                        finish();
                                    }
                                })
                        .setPositiveButton("Azul",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        cancion_menu.stop();
                                        pasomain.putExtra("PartidaGuardada",false);
                                        pasomain.putExtra("Carta","cartaa");
                                        startActivity(pasomain);
                                        finish();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        boton_instrucciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancion_menu.stop();
                startActivity(pasoinstrucciones);
                finish();
            }
        });

        boton_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancion_menu.stop();
                finish();
            }
        });


        boton_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pg){
                    cancion_menu.stop();
                    pasomain.putExtra("PartidaGuardada",true);
                    pasomain.putExtra("Carta","");
                    startActivity(pasomain);
                    finish();
                }else{
                    DisplayToast(Menu.this,"No hay partida guardada");
                }
            }
        });
    }

    public void cargarpartida() throws IOException {

        BufferedReader fin =  new BufferedReader(  new InputStreamReader(openFileInput("mazo.txt")));
        mazocargado = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("mazo2.txt")));
        mazo2cargado = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("pozo.txt")));
        pozocargado = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("matriz.txt")));
        matrizcargada = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("jugadas.txt")));
        jugadascargada = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("MazoOriginal.txt")));
        mazooriginal = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("contador.txt")));
        String num = fin.readLine();
        NumeroJugadas =  Integer.parseInt(num);

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("tiempo.txt")));
        num = fin.readLine();
        tiempobase =  Integer.parseInt(num);

        fin.close();
    }
    public void DisplayToast(Context context, String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

}
