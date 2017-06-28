package com.example.diego.piramidefinalisima;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    /*** ***** Variables para las cartas**/
    Stack<Carta> pozo = new Stack<>();          /*** Pila de cartas para el descarte **/
    Stack<Carta> pozoTemp = new Stack<>();      /*** Pila de cartas para el descarte cargado desde un archivo**/
    Stack<Carta> mazo = new Stack<>();          /*** Pila de cartas para el mazo escondido**/
    Stack<Carta> mazoTemp = new Stack<>();      /*** Pila de cartas para el mazo escondido cargado desde un archivo**/
    Stack<Carta> mazo2 = new Stack<>();         /*** Pila de cartas para el mazo mostrado**/
    Stack<Carta> mazo2Temp = new Stack<>();     /*** Pila de cartas para el mazo mostrado cargado desde un archivo**/
    Stack<Carta> MazoOriginal = new Stack<>();  /*** Pila de cartas para el mazo original cargado desde archivo**/
    Carta[][] TableroCartas = new Carta[7][7];  /*** Matriz para simbolizar las cartas en la mesa**/
    Carta cartaNull = new Carta();              /*** Null establecido para el objeto carta**/
    String pozocargado = "";                    /*** Strings donde se cargan inicialmente las pilas y matriz desde archivo**/
    String mazocargado = "";
    String mazo2cargado = "";
    String matrizcargada = "";
    String jugadascargada = "";
    String mazooriginal = "";
    int  elevation = 1;                         /*** Valor inicial para la elevacion de las cartas**/
    int CartasenMazo = 24;                      /*** Total de cartas en el mazo escondido**/
    float ZoomMax = 3;                          /*** Valores en 3 niveles para hacerle zoom a las cartas en pantalla**/
    float ZoomMid = 2;
    float NoZoom = 1;
    float Width,Height,Mazo1x,Mazo2x,PozoX,LineaY,cuadrado,anchocarta,altocarta,anchoespaciado,ZoomLimMax,ZoomLimMid;
    /*** Variables que se setean en el onCreate() para definir las posiciones de las cartas en los distintos casos: mazo, mazo2**/
    /*** pozo y en el tablero, asi como también sus anchos y altos en base a las dimensiones de la pantalla.**/
    Stack<int[]> Jugadas = new Stack<>();       /*** Pila que guarda los largos del mazo1 mazo2 y del pozo, con las diferencias en estos largos se puede saber que jugadas se hacen y con ello deshacerlas**/
    String[] Pintas = {"d","p","c","t"};        /*** Vector que contiene las pintas para inicializar las cartas**/
    ArrayList<Carta> cartas = new ArrayList<Carta>();/*** Pila donde se guadan todas las cartas antes de repartirlas al mazo o a la mesa.**/
    int NumeroJugadas = 0;                      /*** Contador de jugadas**/
    TextView textJugadas;                       /*** Textview para mostrar cuantas jugadas van. Jugadas son, sacar cartas del mazo al mazo visible, y mandar cartas al pozo.**/
    Boolean PartidaGuardada = true;            /*** Boolean que define si hay partida guardada, por defecto no hay partida guardada (false).**/
    int tiempobase = 0;                         /*** Desfase de tiempo, por defecto 0**/
    String cartastring,cartacargada;

    /*** ***** Variables Cronometro**/

    private Chronometer cronometro;             /***Objeto cronometro para medir el tiempo de juego.**/
    private MediaPlayer cancion_juego;

    /*** Sobrecarga de operador onBackPress() para retornar al menu **/
    @Override
    public void onBackPressed() {
        finish();
        cancion_juego.stop();
        Intent pasomenu = new Intent(this, Menu.class);
        startActivity(pasomenu);
    }

    @Override protected void onPause() {
        super.onPause();
        cancion_juego.stop();
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); /*** Se utiliza el .xml de activity main**/
        /****** Variables para grafica *****/
        getSupportActionBar().hide();           /*** Se esconde la barra superior**/
        final DisplayMetrics metrics = new DisplayMetrics();/*** Obtener las metricas de la pantalla.**/
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final ConstraintLayout cl = (ConstraintLayout) findViewById(R.id.cl1);/*** Se infla el constraint layout para asignar views despues**/
        cl.setBackground(getResources().getDrawable(R.drawable.fondotablero1));/*** Se le asigna el fondo a cl1**/
        Width = metrics.widthPixels;    /*** Ancho de la pantalla en pixeles**/
        Height = metrics.heightPixels;  /*** Alto de la pantalla en pixeles**/
        Mazo1x = 1*Width/10;            /*** Ubicacion(x) del mazo con cartas escondidas**/
        Mazo2x = 5*Width/20;            /*** Ubicacion(x) del mazo con cartas descubiertas**/
        PozoX = 8*Width/10;             /*** Ubicacion(x) del pozo de descarte**/
        LineaY = 7*Height/10;           /*** Ubicación(y) del mazo1,mazo2 y descarte.**/
        cuadrado = Width/25;            /*** Se definen los anchos y altos de las cartas en base a una porción de la pantalla**/
        anchocarta = 3*cuadrado;        /*** Ancho de las cartas en pantalla**/
        altocarta = 3*anchocarta/2;     /*** Alto de las cartas en pantalla**/
        anchoespaciado = cuadrado/2;    /*** Espaciado entre las cartas en el tablero**/
        ZoomLimMax = 70*Height/100;     /*** Desde que lugar de la pantalla funciona el zoom maximo**/
        ZoomLimMid = 50*Height/100;     /*** Desde que lugar de la pantalla funciona el zoom mediano**/

        /******* Variables MediaPlayer**/
        cancion_juego = MediaPlayer.create(this,R.raw.cancionjuego); /*** Se inicializa el objeto mediaplayer con la cancion del juego**/
        cancion_juego.setLooping(true);     /*** Se genera el loop infinito de la cancion.**/
        cancion_juego.start();              /*** Comienza a sonar la canción**/

        /**********Setear boton salir *****************/
        Button boton_salir = (Button) findViewById(R.id.salirjuego); /*** Se infla el boton de salirjuego del xml**/
        boton_salir.setOnClickListener(new View.OnClickListener() { /*** Se le define un onClick() al boton salir donde detiene la canción y finaliza la actividad**/
        @Override
        public void onClick(View v) {
            finish();
            cancion_juego.stop();
            Intent pasomenu = new Intent(MainActivity.this,Menu.class);
            startActivity(pasomenu);
        }
        });

        /**********Setear boton reiniciar *****************/

        Button boton_reiniciar = (Button) findViewById(R.id.reinicar);
        boton_reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancion_juego.stop();
                reiniciarActivity(MainActivity.this);
            }
        });

        /************* Genera mazo de cartas **********************/

        Carta cartaArriba = new Carta(); /*** Objeto carta que va encima de las cartas del mazo escondido**/
        for (String pinta: Pintas){     /*** Se genera el mazo con las cartas del 1 al 13 y con las pintas correspondientes**/
            for (int Numero = 1 ;  Numero <= 13 ; Numero++) {
                cartas.add(new Carta(Numero,pinta));
            }
        }


        /************* Comprobar si hay partida guardada***********/


        String PG = getIntent().getExtras().get("PartidaGuardada").toString();
        PartidaGuardada = Boolean.parseBoolean(PG);

        /************* Sacar el color de la carta ****************/
        cartastring = getIntent().getExtras().get("Carta").toString();



        /******************************/
        if(PartidaGuardada){ /*** Se comprueba si hay una partida guardada.**/
            /********* Se carga donde estaban las cartas***********/
            try {
                cargarpartida();    /*** La función cargar partida nos entregas los string desde los txt.**/
            } catch (IOException e) {
                e.printStackTrace();
            }
            TableroCartas = StringTablero(matrizcargada);   /*** Se transforma el string matrizcargada en una matriz para las cartas en la mesa**/
            mazo = StringStack(mazooriginal);               /*** Se transforma el string mazooriginal en una pila con las cartas del mazo.**/
            Jugadas = JugadaStack(jugadascargada);          /*** Se transforma el string jugadascargada en una pila con las jugadas de la partida guardada**/
            cartastring = cartacargada;

            mazoTemp = StringStack(mazocargado);            /*** Se transforman las string mazocargado,mazo2cargado y pozocargado en pilas.**/
            mazo2Temp = StringStack(mazo2cargado);
            pozoTemp = StringStack(pozocargado);

        }else { /*** Si no hay partida guardada**/
            /*** ************ Revuelve el mazo *******************/
            Collections.shuffle(cartas);
            /*** ************ Rellena el mazo de cartas escondidas **************/
            while (mazo.size() < CartasenMazo) {
                mazo.push(cartas.get(0));
                cartas.remove(0);
            }
            /*** ************ Tira la cartas al tablero *******************/
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    if (j <= i) {
                        cartas.get(0).setColumna(j);            /*** Se le setea la columna al objeto carta**/
                        cartas.get(0).setFila(i);               /*** Se le setea la fila al objeto carta**/
                        cartas.get(0).setEsTablero(true);       /*** Se le setea que la carta está en el tablero**/
                        TableroCartas[j][i] = cartas.get(0);    /*** Se asigna el objeto a la posicion [j][i] de la matriz**/
                        cartas.remove(0);                       /*** Se remueve la carta del mazo original**/
                    } else {
                        TableroCartas[j][i] = cartaNull;        /*** Si no corresponde al triangulo inferior de la matriz se le asigna cartaNull.**/
                    }
                }
            }

            /*** ************ Jugadas Inicial *************************/

            int[] Jinicial = {mazo.size(),mazo2.size(),pozo.size()};
            Jugadas.push(Jinicial);
            NumeroJugadas = 0;
            /*** Se guarda la primera jugada por defecto, mazo completo, mazo2 vacio y pozo vacio.**/
            try {
                /*** Guardamos el tablero de cartas, el mazo original y la primera jugada.**/
                guardarMatriz(TableroCartas);
                guardarMazoOriginal(mazo);
                guardarJugadas();
                guardarcontador();
                guardarCarta();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /*** ************ MANEJO DE INTERFAZ **********************************/





        /*** ************ MANEJO DEL TABLERO *********************************/

        for(int i = 7 ; i > 0 ; i--){
            for(int j = 0; j < i ; j++) { /*** Se recorre el triangulo inferior de la matriz**/
                int id = getApplicationContext().getResources().getIdentifier(TableroCartas[j][i-1].getString(),"drawable",getApplicationContext().getPackageName()); /*** Buscamos el drawable en base al tag del objeto carta pinta+valor**/
                ImageView img = new ImageView(this);    /*** imageview para asignar esta nueva carta**/
                img.setImageResource(id);               /*** al imageview se le asigna el drawable obtenido desde el tag**/
                cl.addView(img);                        /*** Se agrega el imageview al layout**/
                img.setTranslationZ(i);                 /*** Seteamos la translacion en Z para que se levante**/
                img.getLayoutParams().width = (int) anchocarta; /*** Ancho de la carta en pantalla**/
                img.getLayoutParams().height = (int) altocarta; /*** Alto de la carta en pantalla**/
                float espaciadoinicial = (float)0.5 + (float)1.75*(7-i); /*** Función que define la ubicación en X de las cartas en pantalla**/
                img.setX(espaciadoinicial*cuadrado + j * anchoespaciado + j * anchocarta); /*** Se pone la carta en la la ubicación correspondiente, considerando ahora las dimensiones de la carta**/
                img.setY(i*Height/15); /*** Se asigna la ubicación de la carta en Y**/
                img.setTag(TableroCartas[j][i-1].getString());  /*** Le ponemos un tag al imageview, igual al tag (getString()) del objeto carta pinta+valor**/
                final float xInicial = img.getX();  /*** Guardamos la posición inicial del imageview en X**/
                final float yInicial = img.getY();  /*** Guardamos la posición inicial del imageview en Y**/
                final float zInicial = img.getTranslationZ();  /*** Guardamos la posición inicial del imageview en Z**/

                TableroCartas[j][i-1].setXY_WH(espaciadoinicial*cuadrado + j * anchoespaciado + j * anchocarta,i*Height/15,anchocarta,altocarta);
                /*** Guardamos en el objeto carta de la matriz los valores de X,Y,W y H, del imageview correspondiente al mismo.**/

                img.setOnTouchListener(new View.OnTouchListener() { /*** Le asignamos un onTouchListener() al imageview**/
                @Override
                public boolean onTouch(View view,MotionEvent event){
                    /*** Obtenemos las coordenadas en X e Y del evento, más las coordenadas de la cartas, para definir las coordenadas del movimiento en la pantalla**/
                    float movX = view.getX() + event.getX() - (view.getWidth())/2;
                    float movY = view.getY() + event.getY() - (view.getHeight())/2;

                    String tag = (String) view.getTag();                            /*** Tag de la primera carta tocada.**/
                    String OtroTag = findCartaByXY( movX , movY ,TableroCartas );   /*** Tag de la segunda carta tocada, en este caso por el evento final.**/

                    if(CartaNoPozo(tag)  && CartaLibre(tag)){ /*** Vemos que la carta tomada no esté en el pozo, y que además no tenga cartas que la cubran**/
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                view.setTranslationZ(8);    /***Levantamos la carta para que se vea**/
                                view.setScaleX(ZoomMid);    /***Le aplicamos el zoom para que se vea mejor que carta es**/
                                view.setScaleY(ZoomMid);    /***este zoom se acaba se si se mueve la carta**/
                                break;
                            case MotionEvent.ACTION_MOVE:
                                view.setX(movX);            /***Se mueve en X la carta tomada**/
                                view.setY(movY);            /***Se mueve en Y la carta tomada**/
                                view.setScaleX(NoZoom);     /***Dehacemos el zoom a la carta**/
                                view.setScaleY(NoZoom);
                                view.postInvalidate();
                                break;
                            case MotionEvent.ACTION_UP:
                                view.setTranslationZ(zInicial); /*** Cuando levantamos la selección la carta vuelve a su ubicación en Z original.**/
                                view.setScaleX(NoZoom);         /***Dehacemos el zoom a la carta**/
                                view.setScaleY(NoZoom);
                                if (CompruebaKaiser(tag)){  /*** Comprobamos si la carta tomada es un kaiser, valor == 13**/
                                    MandarAlPozo(tag);      /*** Si es kaiser, se va al pozo de descarte**/
                                    /***** Guardar las cartas que estan en el pozo ****/
                                    try {
                                        guardarPozo();
                                    } catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    /**************************************************/
                                }else if(CompruebaSumaCartas(tag, OtroTag) && (CartaLibre(OtroTag) || jugadaDesdeTablero(tag,OtroTag))) {
                                    /*** Si es un evento sobre otra carta en el UP, comprobamos que la suma sea 13 y que además se cumpla que la otra carta del evento esté libre o que se cumpla la regla de jugada desde el tablero si están consecutivas.**/
                                    MandarAlPozo(tag);          /*** Enviamos ambas cartas al pozo de descarte, si lo anterior se cumple.**/
                                    MandarAlPozo(OtroTag);
                                    /***** Guardar las cartas que estan en el pozo ****/
                                    try {
                                        guardarPozo();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    /**************************************************/
                                }else{
                                    view.setX(xInicial);    /*** Si nada de lo anterior se cumple, la carta vuelve a su posición inicial.**/
                                    view.setY(yInicial);
                                }
                                view.postInvalidate();

                                ContarCartas();             /*** Contamos las cartas para guardar la jugada**/
                                /***** Guardar contador de jugadas ****************/
                                try {
                                    guardarcontador();
                                    guardarJugadas();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                /**************************************************/
                                if(CompruebaGanar()){
                                    finish();
                                    cancion_juego.stop();
                                    Intent pasoganar = new Intent(MainActivity.this,Ganar.class);
                                    startActivity(pasoganar);
                                }
                                if(!Comprobarpierde()){     /*** Luego de que se complete la jugada , se revisa si el jugador pierde.**/
                                    DisplayToast(getApplicationContext(),"No quedan movimiento válidos");
                                }



                                break;
                        }
                    }
                    return true;
                }

                });
            }
        }


        /*** ************ MANEJO DEL MAZO *********************************/

        for(int i = 0; i <= mazo.size(); i++) {
            int id;
            if(i<mazo.size()) {
                /*** Si es una carta del mazo, obtenemos su tag, pinta+valor, y obtenemos el drawable correspondiente.**/
                id = getApplicationContext().getResources().getIdentifier(mazo.get(i).getString(), "drawable", getApplicationContext().getPackageName());
            }else{
                /*** Si es la carta que va sobre el mazo escondido, entonces se le asigna el drawable de la carta boca abajo.**/
                id = getApplicationContext().getResources().getIdentifier(cartastring, "drawable", getApplicationContext().getPackageName());
            }
            ImageView img = new ImageView(this);    /*** Genermos el nuevo imageview.**/
            img.setImageResource(id);               /*** Le asignamos el drawable previamente obtenido**/
            cl.addView(img);                        /*** ponemos el imageview en el layout**/

            img.getLayoutParams().width = (int) anchocarta; /*** Le asignamos el ancho a la carta en la pantalla**/
            img.getLayoutParams().height = (int) altocarta; /*** Le asignamos el alto a la carta en la pantalla**/

            img.setX(Mazo1x);   /*** Ponemos la carta en la posición (X) del mazo escondido**/
            img.setY(LineaY);   /*** Ponemos la carta en la posición (Y) del mazo escondido**/
            img.setTranslationZ(mazo.size()-i); /*** Le asignamos una elevación en Z desde mayor a menor para que queden ordenadas**/
            if(i<mazo.size()){
                /*** Para las cartas dentro del mazo escondido, le asignamos el tag a sus imageviews y además guardas X,Y,W y H en el objeto carta.**/
                img.setTag(mazo.get(i).getString());
                mazo.get(i).setXY_WH(Mazo1x,LineaY,anchocarta,altocarta);
            }else{
                /*** Para la carta encima del mazo, esto es más particular.**/
                cartaArriba.setXY_WH(Mazo1x,LineaY,anchocarta,altocarta);
                img.setTag("0carta");           /*** tag de carta null**/
                cartaArriba.setPinta("carta");  /*** pinta generica**/
                img.setTranslationZ(mazo.size()+1); /*** elevacion por sobre el mazo**/
            }

            final float zInicial = img.getTranslationZ(); /***obtenemos la elevacion en z inicial del imageview**/

            /*** ************ Manejo OnTouch() Mazo1 y Mazo2 *************************/
            if(i<mazo.size()) {     /*** Para las cartas en el mazo escondido se tiene el siguiente onTouch() :**/
                img.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view,MotionEvent event){
                        /*** Se obtienen las coordenadas del evento, más las coordenadas del view, se puede generar el movimiento**/
                        float movX = view.getX() + event.getX() - (view.getWidth())/2;
                        float movY = view.getY() + event.getY() - (view.getHeight())/2;
                        /*** Obtenemos el tag de la carta tomada y de la carta se genere el evento final.**/
                        String OtroTag = findCartaByXY( movX , movY , TableroCartas );
                        String tag = (String) view.getTag();


                        if(CartaNoPozo(tag)){ /*** Comprobamos que la carta tomada no esté en el pozo**/
                            switch (event.getAction()){
                                case MotionEvent.ACTION_DOWN:   /*** Para la accion de presionar**/
                                    view.setTranslationZ(100);  /*** Se le eleva la carta para que quede sobre todo el resto**/
                                    view.setScaleX(ZoomMax);    /*** Se genera el zoom maximo al tomarla para que se vea mejor**/
                                    view.setScaleY(ZoomMax);
                                    break;

                                case MotionEvent.ACTION_MOVE:
                                    view.setX(movX); /*** Generamos el movimiento en x e y del imageview carta.**/
                                    view.setY(movY);
                                    if(movY > ZoomLimMax){          /*** Comprobamos que la carta esté en la region donde haya maximo zoom**/
                                        view.setScaleX(ZoomMax);
                                        view.setScaleY(ZoomMax);
                                    }else if(ZoomLimMax > movY && movY > ZoomLimMid){/*** Comprobamos que la carta esté en la region donde haya zoom mediano**/
                                        view.setScaleX(ZoomMid);
                                        view.setScaleY(ZoomMid);
                                    }else{
                                        view.setScaleX(NoZoom); /*** Si no está en ninguna de estas regiones, o sea si es está más cerca del tablero, se le elimina el zoom.**/
                                        view.setScaleY(NoZoom);
                                    }
                                    view.postInvalidate();
                                    break;

                                case MotionEvent.ACTION_UP:         /*** Cuando se levanta el onTouch()**/
                                    view.setTranslationZ(zInicial+1/2); /*** Se vuelve a su elevacion inicial el imageview**/
                                    view.setScaleX(NoZoom);         /*** Se elimina el zoom , independiente donde esté la carta**/
                                    view.setScaleY(NoZoom);

                                    if(CompruebaKaiser(tag)){       /*** Si la carta es un Kaiser**/
                                        MandarAlPozo(tag);          /*** Se envia directamente al pozo**/
                                        /***** Guardar cartas en mazo, mazo2 y pozo *******/
                                        try {
                                            guardarPozo();
                                            guardarMazo();
                                            guardarMazo2();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        /**************************************************/
                                    }else if(CompruebaSumaCartas(tag,OtroTag)&&CartaLibre(OtroTag)){
                                        /*** Si la carta tomada es soltada sobre otra (Tablero) se comprueba que la suma de ambas de 13 y que además la carta del tablero esté libre.**/
                                        MandarAlPozo(tag);      /*** Si lo anterior se cumple, ambas cartas se van al pozo**/
                                        MandarAlPozo(OtroTag);
                                        /***** Guardar cartas en mazo, mazo2 y pozo *******/
                                        try {
                                            guardarPozo();
                                            guardarMazo();
                                            guardarMazo2();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        /**************************************************/
                                    }else{ /*** Si no es kaiser , ni es un movimiento valido con el tablero, la carta es devuelta al mazo descubierto**/
                                        view.setX(Mazo2x);
                                        view.setY(LineaY);
                                    }
                                    view.postInvalidate();

                                    ContarCartas(); /*** Contamos las cartas para guardar la jugada realizada**/
                                    /***** Guardar contador de jugadas ****************/
                                    try {
                                        guardarcontador();
                                        guardarJugadas();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    /**************************************************/
                                    if(CompruebaGanar()){
                                        finish();
                                        cancion_juego.stop();
                                        Intent pasoganar = new Intent(MainActivity.this,Ganar.class);
                                        startActivity(pasoganar);
                                    }
                                    if(!Comprobarpierde()){ /*** Comprobamos si después de realizada la jugada el jugador pierde**/
                                        DisplayToast(getApplicationContext(),"No quedan movimiento válidos");
                                    }
                                    break;
                            }
                        }
                        return true;
                    }

                });
            }/****************** Fin manejo Mazo1 y Mazo2************/

            /*** *************** Manejo carta superior **************/
            else{
                img.setOnTouchListener(new View.OnTouchListener() { /*** Para sacar cartas del mazo escondido al mazo descubierto se usa este onTouch()**/
                @Override
                public boolean onTouch(View view,MotionEvent event){
                    ImageView imgv = (ImageView) findViewById(R.id.cl1).findViewWithTag(view.getTag());
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(!mazo.empty()){ /*** Al presionar, si el mazo escondido no está vacio**/
                                mazo2.push(mazo.peek());    /*** Se toma la carta superior del mazo escondido y se pone en el descubierto**/
                                findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setX(Mazo2x); /*** Seteamos la posicion en X de la carta sacada**/
                                findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setTranslationZ(elevation+1);   /*** Seteamos la elevacion de la carta sacada**/
                                elevation = elevation + 1;
                                mazo.pop(); /*** Se elimina la carta saca del mazo escondido al mazo descubierto.**/
                                /***** Guardar cartas en mazo, mazo2 y pozo *******/
                                try {
                                    guardarPozo();
                                    guardarMazo();
                                    guardarMazo2();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                /**************************************************/
                            }else{      /*** Si el mazo escondido está vacío**/
                                int id = getApplicationContext().getResources().getIdentifier(cartastring, "drawable", getApplicationContext().getPackageName());
                                imgv.setImageResource(id); /***Se asigna el drawable de la carta dada vuelta encima**/
                                elevation = 1;
                                while(!mazo2.empty()){ /*** Vaciamos el mazo descubierto en el escondido.**/
                                    mazo.push(mazo2.peek());    /*** Ponemos la carta superior del mazo descubierto en el escondido**/
                                    mazo2.pop();                /*** Sacamos la carta del mazo descubierto**/
                                    findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setX(Mazo1x);                   /*** Ponemos su posicion en X en el mazo escondido**/
                                    findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setTranslationZ(++elevation);   /*** Arreglamos la elevacion de las cartas**/
                                }
                                elevation = 1;
                                /***** Guardar cartas en mazo, mazo2 y pozo *******/
                                try {
                                    guardarPozo();
                                    guardarMazo();
                                    guardarMazo2();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                /**************************************************/
                            }
                            break;
                        case MotionEvent.ACTION_UP: /*** En la accion de levantar**/
                            if(mazo.empty()){   /*** Si el mazo quedó vacío**/
                                imgv.setImageResource(R.drawable.cartavacia); /*** Se le asigna la imagen de carta vacia**/
                            }
                            ContarCartas(); /*** Contamos las cartas para guardar la jugada realizada**/
                            /***** Guardar contador de jugadas ****************/
                            try {
                                guardarcontador();
                                guardarJugadas();
                                guardarPozo();
                                guardarMazo();
                                guardarMazo2();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            /**************************************************/
                            break;
                    }
                    return true;
                }
                });
            }/*******************Fin manejo carta superior **************/

        }/*** *******************Fin for() del mazo**********************/


        /*** ********************Se reordenan las cartas en base a lo guardado****/
        int Pozoidx = 0; /*** El indice inicial para ver el Pozo cargado es 0**/
        if(PartidaGuardada){
            /*** Reordenamos las cartas en base a lo entregado por la pila de jugadas**/
            for(int i = 0 ; i < Jugadas.size()-1 ; i++){
                int [] jugadat0 = Jugadas.get(i);   /*** Obtenemos la jugada en tiempo 0**/
                int m1t0 = jugadat0[0];             /*** Tamaño del mazo 1 en tiempo 0**/
                int m2t0 = jugadat0[1];             /*** Tamaño del mazo 2 en tiempo 0**/
                int pt0 = jugadat0[2];              /*** Tamaño del pozo en tiempo 0**/
                int [] jugadat1 = Jugadas.get(i+1); /*** Obtenemos la jugada en tiempo 1**/
                int m1t1 = jugadat1[0];             /*** Tamaño del mazo 1 en tiempo 1**/
                int m2t1 = jugadat1[1];             /*** Tamaño del mazo 2 en tiempo 1**/
                int pt1 = jugadat1[2];              /*** Tamaño del pozo en tiempo 1**/
                int deltam1 = m1t0 - m1t1;          /*** Calculamos los delta m1, m2 y p**/
                int deltam2 = m2t0 - m2t1;
                int deltap  = Math.abs(pt0 - pt1);

                if(deltam1 > 0){ /*** Si hubo un cambio en el mazo 1**/
                    if(!mazo.empty()) { /*** y si el mazo 1 no está vacío**/
                        mazo2.push(mazo.peek());    /*** Pasamos la carta de encima del mazo 1 al mazo 2**/
                        findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setX(Mazo2x); /*** Ubicamos la carta mazo 2 en la pantalla**/
                        findViewById(R.id.cl1).findViewWithTag(mazo.peek().getString()).setTranslationZ(elevation); /*** Seteamos su elevación para que quede encima**/
                        elevation = elevation + 1;
                        mazo.pop(); /***Sacamos la carta del mazo 1**/
                    }
                } else if(deltap > 0){/*** Si hubo un cambio en el tamaño del pozo**/
                    Carta cartaAlFondo = pozoTemp.get(Pozoidx); /*** Tomamos la "primera" carta del pozo**/
                    Pozoidx++;                                  /***Avanzamos en 1 el indice del pozo**/
                    String tag = cartaAlFondo.getString();
                    if(CompruebaKaiser(tag)){/*** Si es un kaiser, buscamos esta carta y la enviamos al pozo**/
                        MandarAlPozo(tag);
                    }else{ /*** Si no es kaiser, buscamos la siguiente carta que es la compañera para que sume 13 con la primera**/
                        String OtroTag = pozoTemp.get(Pozoidx).getString();
                        Pozoidx++; /*** avanzamos el indice del pozo**/
                        MandarAlPozo(tag); /*** Enviamos ambas al pozo**/
                        MandarAlPozo(OtroTag);
                    }
                }
            }
        }

        /************* Setear boton de desahacer jugada ***********/

        Button undo = (Button) findViewById(R.id.undo); /*** Inflamos el boton de deshacer**/

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeshacerJugada(); /*** Deshacemos la jugada en base a la pila de jugadas.**/
                /******* guardamos el pozo, y los mazos.**/
                try {
                    guardarPozo();
                    guardarMazo();
                    guardarMazo2();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /**************************************************/
            }
        });

        /************* Setear cronometro **************************/

        cronometro = (Chronometer) findViewById(R.id.cronometro); /*** Inflamos el objeto cronometro**/
        cronometro.start(); /*** lo iniciamos**/
        cronometro.setTextSize(20); /*** le damos tamaño 20 a sus numeros**/
        cronometro.setGravity(1); /*** gravedad del cronometro 1**/

        cronometro.setBase(SystemClock.elapsedRealtime() - tiempobase); /*** Seteamos la base, la cual cambia si habia una partida guardada**/
        findViewById(R.id.cronometro).setX(44*Width/100); /*** Ponemos su ubicacion (X) en la pantalla**/
        findViewById(R.id.cronometro).setY(2*Height/100); /*** Ponemos su ubicacion (Y) en la pantalla**/
        findViewById(R.id.cronometro).setBackgroundColor(Color.WHITE); /*** Dejamos su fondo en blanco**/

        cronometro.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                String tiempo = chronometer.getText().toString(); /*** Se obtiene el tiempo actual como string**/
                try{
                    guardarTiempo(tiempo); /*** Guardamos el tiempo**/
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(tiempo.equalsIgnoreCase("01:00")){ /*** Avisa cuando se cumple un minuto**/
                    DisplayToast(getApplicationContext(),"Va 1 minuto");
                }else if(tiempo.substring(2,tiempo.length()).equalsIgnoreCase(":00") && !tiempo.equals("00:00")){ /*** Avisa que van X minutos**/
                    DisplayToast(getApplicationContext(),"Van "+ tiempo.substring(1,2) +" minutos");
                }
            }
        });

        /*** ********************Numero de jugadas********************************/
        textJugadas = (TextView) findViewById(R.id.jugadas);    /***Inflamos el textview**/
        textJugadas.setX(Width/4);                              /*** Seteamos su posicion en X**/
        textJugadas.setY(75*Height/100);                        /*** Seteamos su posicion en Y**/
        textJugadas.setText(Integer.toString(NumeroJugadas));   /*** Ponemos como texto la cantidad de jugadas realizadas**/
        /*** Jugadas se considera:
         /*** Cuando se saca una carta desde el mazo.
         /*** Cuando se mandan cartas al pozo.
         /**************************/

        /*** ********************Fin OnCreate() **********************************/
    }




    /*** *************************** FUNCIONES ************************************/

    /***Función para realizar el toast en pantalla en la aplicacion**/
    public void DisplayToast(Context context, String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
    /***Función que busca la carta en base a su tag y la envía al pozo tanto en pantalla como en la pila lógica.**/
    public void MandarAlPozo(String tag){
        pozo.push(findCartaByTag(tag));
        findViewById(R.id.cl1).findViewWithTag(tag).setX(PozoX);
        findViewById(R.id.cl1).findViewWithTag(tag).setY(LineaY);
        findViewById(R.id.cl1).findViewWithTag(tag).setTranslationZ(pozo.size());
        if(!findCartaByTag(tag).getEsTablero()) {
            mazo2.pop();
        }
    }
    /***Comprueba si no quedan movimiento validos y muestra si es que el jugador perdió, no genera un cambio de actividad para darle la opción al jugador de deshacer y poder intentar otra combinacion***/
    public boolean Comprobarpierde(){

        for(int i = 0 ; i < 7 ; i++){
            for(int j = 0 ; j <= i ; j++ ){
                String auxCarta = TableroCartas[j][i].getString();
                if(CartaLibre(auxCarta) && CartaNoPozo(auxCarta)) {
                    for (int m1 = 0 ; m1 < mazo.size() ; m1++){
                        if(CompruebaSumaCartas(auxCarta,mazo.get(m1).getString())){
                            return true;
                        }
                    }
                    for (int m2 = 0 ; m2 < mazo2.size() ; m2++){
                        if(CompruebaSumaCartas(auxCarta,mazo2.get(m2).getString())){
                            return true;
                        }
                    }
                }

            }
        }

        for(int i = 0 ; i < 7 ; i++){
            for(int j = 0 ; j <= i ; j++ ){
                String auxCarta1 = TableroCartas[j][i].getString();
                for(int k = 0 ; k < 7 ; k++){
                    for(int l = 0 ; l<=k ; l++){
                        String auxCarta2 = TableroCartas[l][k].getString();

                        if(jugadaDesdeTablero(auxCarta1,auxCarta2)){
                            return true;}
                    }

                }

            }
        }

        return false;
    }
    /***Comprueba si la carta tomada es uno de los 4 kaiser**/
    public Boolean CompruebaKaiser(String tag){
        return ( tag.equals("c13") || tag.equals("d13") || tag.equals("p13") || tag.equals("t13") );
    }
    /***Comprueba si el jugador ganó, eso se logra revisando que todas las cartas en la mesa estén en el pozo**/
    public Boolean CompruebaGanar(){
        int contador = 0;
        for(int i = 0 ; i < 7 ; i++){
            for(int j = 0 ; j <= i ; j++){
                if(!CartaNoPozo(TableroCartas[j][i].getString())){
                    contador++;
                }
            }
        }
        return contador == 28;
    }
    /***Comprueba en base a los tag de las dos cartas si la suma de sus valores es igual a 13**/
    public Boolean CompruebaSumaCartas(String tag1 , String tag2){
        if(!tag2.equals("***")) {
            int Valor1 = Integer.parseInt(tag1.substring(1,tag1.length()));
            int Valor2 = Integer.parseInt(tag2.substring(1,tag2.length()));
            return (Valor1 + Valor2 == 13);
        }else{
            return false;
        }
    }
    /***Comprueba si la carta con el tag tomado se encuentra o no en el pozo**/
    public Boolean CartaNoPozo(String tag){
        if(!pozo.empty()) {
            return pozo.search(findCartaByTag(pozo, tag)) == -1;
        }else{
            return true;
        }
    }
    /***Verifica si es que se realizó una jugada entre cartas del tablero si es que esta es valida o no**/
    public boolean jugadaDesdeTablero(String tag1, String tag2){
        Carta c1 = findCartaByTag(tag1);
        Carta c2 = findCartaByTag(tag2);
        Carta Sup = new Carta();
        Carta Inf = new Carta();
        if(c1.getFila() > c2.getFila()){
            Sup = new Carta(c2);
            Inf = new Carta(c1);
        }else if(c1.getFila() < c2.getFila()){
            Sup = new Carta(c1);
            Inf = new Carta(c2);
        }else{
            return false;
        }

        int ColumnaInf = Inf.getColumna();
        int ColumnaSup = Sup.getColumna();
        int FilaInf = Inf.getFila();
        int FilaSup = Sup.getFila();
        System.out.println("|F I :" + FilaInf +"|F S :" + FilaSup +"|C I :" + ColumnaInf +"|C S :" + ColumnaSup);
        if(FilaInf!=6){
            if( CompruebaSumaCartas(tag1,tag2) && ((ColumnaInf == ColumnaSup && FilaSup+1 == FilaInf && CartaNoPozo(TableroCartas[FilaInf+1][ColumnaInf+1].getString())) || (ColumnaInf == ColumnaSup+1 && FilaSup+1 == FilaInf && CartaNoPozo(TableroCartas[FilaInf+1][ColumnaInf].getString()))) && CartaLibre(Inf.getString())){

                return true;

            }
        }else{
            if( CompruebaSumaCartas(tag1,tag2) && ((ColumnaInf == ColumnaSup && FilaSup+1 == FilaInf ) || (ColumnaInf == ColumnaSup+1 && FilaSup+1 == FilaInf ) )) {
                return true;
            }
        }

        return false;
    }
    /***Verifica que la carta tomada o donde fue soltada otra carta en la mesa, está libre para poder ser eliminada**/
    public Boolean CartaLibre(String tag){
        int F = findCartaByTag(tag).getFila();
        int C = findCartaByTag(tag).getColumna();
        if(CartaNoPozo(tag)) {
            if (F == 6 || C == -1) {
                return true;
            } else {
                return (!(CartaNoPozo(TableroCartas[C][F + 1].getString())) && !(CartaNoPozo(TableroCartas[C + 1][F + 1].getString())));
            }
        }else{
            return false;
        }
    }

    /***En base a las coordenadas X e Y entregadas por el evento busca la carta correspondiente en la matriz para retornar su tag**/
    public String findCartaByXY(float x , float y , Carta[][] Tablero){
        String tag = "***";
        for(int i = 6; i >= 0 ; i--){
            for(int j = 0 ; j <= i ; j++){
                float xpos = (int) Tablero[j][i].getPosx();
                float ypos = (int) Tablero[j][i].getPosy();
                float ancho = (int) Tablero[j][i].getWidth();
                float largo = (int) Tablero[j][i].getHeight();
                int left = (int) (xpos - ancho/2);
                int top = (int) (ypos - largo/2);
                int right = (int) (xpos + ancho/2);
                int bottom = (int) (ypos + largo/2);
                Rect rect = new Rect(left,top,right,bottom);
                if(rect.contains((int)x,(int)y)){
                    return Tablero[j][i].getString();
                }
            }
        }
        return tag;
    }
    /***En base al tag entregado a la funcion busca la carta correspondiente en la matriz para retornar el objeto carta**/
    public Carta findCartaByTag(Carta[][] Tablero,String tag){
        int Largo = Tablero.length;
        for(int i = 0; i < Largo ; i++){
            for(int j = 0 ; j < Largo ; j++){
                if(Tablero[j][i].getString().equals(tag)){
                    return Tablero[j][i];
                }
            }
        }
        return cartaNull;

    }
    /***En base al tag entregado a la funcion busca la carta correspondiente en la una pila para retornar el objeto carta**/
    public Carta findCartaByTag(Stack<Carta> Pila,String tag){
        Stack<Carta> aux = Pila;

        for(int k = 0; k<aux.size();k++){
            if(aux.get(k).getString().equals(tag)) {
                return aux.get(k);
            }
        }
        return cartaNull;
    }
    /***En base al tag entregado a la funcion busca la carta correspondiente en la arraylist para retornar el objeto carta**/
    public Carta findCartaByTag(ArrayList<Carta> lista,String tag){
        for(int i = 0 ; i < lista.size() ; i++){
            if(lista.get(i).getString().equals(tag)){
                return lista.get(i);
            }
        }
        return cartaNull;
    }
    /***Funcion generalizada de los otros findCartaByTag donde solo recibe el tag y lo busca usando las otras funciones en todos lados.**/
    public Carta findCartaByTag(String tag){
        Carta c = new Carta();
        if(findCartaByTag(TableroCartas,tag) != cartaNull){
            c = new Carta(findCartaByTag(TableroCartas,tag));
        }else if (findCartaByTag(mazo2,tag) != cartaNull){
            c = new Carta(findCartaByTag(mazo2,tag));
        }
        return c;
    }

    /***Transforma varias pilas en string, es solo con fines de revisar en la impresion del sistema**/
    public String PilastoString(Stack<Carta> stk1,Stack<Carta> stk2,Stack<Carta> stko){
        String Print1 = "Mazo1: |";
        String Print2 = "Mazo2: |";
        String Print3 = "PozoD: |";
        String nl = System.getProperty("line.separator");
        for(int k = 0; k<stk1.size();k++){
            Print1 = Print1 + stk1.get(k).getString()+" > ";
        }
        for(int k = 0; k<stk2.size();k++){
            Print2 = Print2 + stk2.get(k).getString()+" > ";
        }

        for(int k = 0; k<stko.size();k++){
            Print3 = Print3 + stko.get(k).getString()+" > ";
        }

        return Print1 +" | " + nl + Print2 +" | "+ nl + Print3+" | ";
    }
    /***Imprime el stack de jugadas, tambien es solo para revisar la pila.**/
    public void ImprimirJugadas(){
        String p = "";
        for(int i = 0 ; i < Jugadas.size() ; i++){
            p = p + "|" + Jugadas.get(i)[0] + ","+ Jugadas.get(i)[1] + ","+ Jugadas.get(i)[2] + "|";
        }
        System.out.println(p);
    }
    /***Imprime el tablero de cartas, solo es para revisar**/
    public void ImprimirTablero(Carta[][] TableroCartas){
        int Largo = TableroCartas.length;
        for(int i = 0; i < Largo ; i++) {
            String Fila = "[ ";
            for (int j = 0; j < Largo; j++) {
                Fila = Fila+TableroCartas[j][i].getString()+" ";
            }
            System.out.println(Fila+" ]");
        }
        for(int i = 0; i < Largo ; i++) {
            String Fila = "[ ";
            for (int j = 0; j < Largo; j++) {
                Fila = Fila+"|x:" +TableroCartas[j][i].getPosx()+"|y:"+TableroCartas[j][i].getPosy()+"|";
            }
            System.out.println(Fila+" ]");
        }
        for(int i = 0; i < Largo ; i++) {
            String Fila = "[ ";
            for (int j = 0; j < Largo; j++) {
                Fila = Fila+"|x:" +TableroCartas[j][i].getColumna()+"|y:"+TableroCartas[j][i].getFila()+"|";
            }
            System.out.println(Fila+" ]");
        }
    }

    /***** Esta seccion guarda todas las variables necesarias para poder cargar la partida después *****/
    public void guardarMazo()throws IOException{
        OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("mazo.txt", Context.MODE_PRIVATE));
        for(int i = 0 ; i < mazo.size() ; i++){
            fout.write(mazo.get(i).getString()+" ");
        }
        fout.close();
    }
    public void guardarCarta()throws IOException{
        OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("cartastring.txt", Context.MODE_PRIVATE));
        fout.write(cartastring);
        fout.close();
    }
    public void guardarTiempo(String t)throws IOException{
        OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("tiempo.txt", Context.MODE_PRIVATE));
        int minutos = Integer.parseInt(t.substring(0,2));
        int segundos = Integer.parseInt(t.substring(3,5));
        int tiempo = (minutos*60+segundos)*1000;
        fout.write(Integer.toString(tiempo));
        fout.close();
    }
    public void guardarMazo2()throws IOException{

        OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("mazo2.txt", Context.MODE_PRIVATE));
        for(int i = 0 ; i < mazo2.size() ; i++){
            fout.write(mazo2.get(i).getString()+" ");
        }
        fout.close();
    }
    public void guardarPozo() throws IOException{
        OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("pozo.txt", Context.MODE_PRIVATE));
        for(int i = 0 ; i < pozo.size() ; i++){
            fout.write(pozo.get(i).getString()+" ");
        }
        fout.close();
    }
    public void guardarMatriz(Carta[][] matrizcarta) throws IOException {
        OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("matriz.txt", Context.MODE_PRIVATE));
        for(int i = 0  ; i < 7 ; i++){
            for(int j = 0 ; j < 7 ; j++){
                fout.write(matrizcarta[j][i].getString()+" ");
            }
        }
        fout.close();
    }
    public void guardarcontador() throws IOException {
        OutputStreamWriter fout = new OutputStreamWriter(openFileOutput("contador.txt", Context.MODE_PRIVATE));
        fout.write(Integer.toString(NumeroJugadas));
        fout.close();
    }
    public void guardarJugadas() throws IOException{
        OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("jugadas.txt", Context.MODE_PRIVATE));
        String save = "";
        for(int i = 0 ; i < Jugadas.size() ; i++){
            save = save + Integer.toString(Jugadas.get(i)[0]) + " "+Integer.toString(Jugadas.get(i)[1])+" "+Integer.toString(Jugadas.get(i)[2])+",";
        }
        fout.write(save);
        fout.close();
    }
    public void guardarMazoOriginal(Stack<Carta> Mazo) throws IOException{
        OutputStreamWriter fout= new OutputStreamWriter(openFileOutput("MazoOriginal.txt", Context.MODE_PRIVATE));
        for(int i = 0 ; i < Mazo.size() ; i++ ){
            fout.write(Mazo.get(i).getString()+" ");
        }
        fout.close();
    }
    /********************************************************************************************************/
    /***Esta funcion carga todas las String en los archivos guardados**/
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

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("cartastring.txt")));
        cartacargada = fin.readLine();

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("contador.txt")));
        String num = fin.readLine();
        NumeroJugadas =  Integer.parseInt(num);

        fin =  new BufferedReader(  new InputStreamReader(openFileInput("tiempo.txt")));
        num = fin.readLine();
        tiempobase =  Integer.parseInt(num);

        fin.close();
    }
    /***La funcion contarcartas revisa los largos del mazo1,mazo2 y del pozo.**/
    public void ContarCartas(){
        int[] jt1 = {mazo.size(),mazo2.size(),pozo.size()};
        int mt0,m2t0,pt0;
        int mt1 = jt1[0],m2t1 = jt1[1],pt1 = jt1[2];
        mt0 = Jugadas.peek()[0];
        m2t0 = Jugadas.peek()[1];
        pt0 = Jugadas.peek()[2];
        if( (mt0 != mt1) || (m2t0 != m2t1) || (pt0 != pt1) ){
            Jugadas.push(jt1);
            /***** Se actualiza el numero de jugadas**/
            NumeroJugadas++;
            textJugadas.setText(Integer.toString(NumeroJugadas));
        }
    }
    /***Utilizando la pila de jugadas es posible ver las diferencias en el tiempo para poder deshacer la ultima jugada realizada**/
    public void DeshacerJugada() {
        if(Jugadas.size()>1){
            int[] jugadat1 = Jugadas.get(Jugadas.size()-1);
            int[] jugadat0 = Jugadas.get(Jugadas.size()-2);
            /************ Calcula diferencias *********/
            int deltam1 = jugadat0[0] - jugadat1[0];
            int deltam2 = jugadat0[1] - jugadat1[1];
            int deltap = Math.abs(jugadat0[2] - jugadat1[2]);
            /************ Recuperar del pozo **********/
            if(deltap > 0 && !pozo.empty()){
                int k = 1;
                if (pozo.peek().getValor() < 13) {
                    k = 2;
                }

                for (int i = 1; i <= k; i++) {
                    Carta c = pozo.peek();
                    String tag = c.getString();
                    System.out.println(tag + " " + c.getFila() + c.getColumna());

                    if (c.getEsTablero()) {
                        findViewById(R.id.cl1).findViewWithTag(tag).setTranslationZ(c.getFila() + 1);
                        findViewById(R.id.cl1).findViewWithTag(tag).setX(c.getPosx());
                        findViewById(R.id.cl1).findViewWithTag(tag).setY(c.getPosy());
                    } else {
                        float elevacion = findViewById(R.id.cl1).findViewWithTag(mazo2.peek().getString()).getTranslationZ();
                        findViewById(R.id.cl1).findViewWithTag(tag).setTranslationZ(elevacion+1);
                        findViewById(R.id.cl1).findViewWithTag(tag).setX(Mazo2x);
                        findViewById(R.id.cl1).findViewWithTag(tag).setY(LineaY);
                        mazo2.push(c);
                    }
                    pozo.pop();
                }
                /************** Se recupera del mazo 2 al mazo 1 *****/
            }
            if(deltam1 > 0 && !mazo2.empty()){
                String tag = mazo2.peek().getString();
                Carta c = mazo2.peek();
                mazo.push(c);
                mazo2.pop();
                findViewById(R.id.cl1).findViewWithTag(tag).setTranslationZ(mazo.size());
                findViewById(R.id.cl1).findViewWithTag(tag).setX(Mazo1x);
                findViewById(R.id.cl1).findViewWithTag(tag).setY(LineaY);
                ImageView cArriba;
                if(jugadat1[0] == 0){
                    cArriba = (ImageView) findViewById(R.id.cl1).findViewWithTag("0carta");
                    int id = getApplicationContext().getResources().getIdentifier(cartastring, "drawable", getApplicationContext().getPackageName());
                    cArriba.setImageResource(id);
                }
            }

            if(jugadat0[1] == jugadat1[0] && jugadat0[0] == 0 && jugadat1[1] == 0){
                while(!mazo.empty()){
                    String tag = mazo.peek().getString();
                    Carta c = mazo.peek();
                    mazo2.push(c);
                    mazo.pop();
                    findViewById(R.id.cl1).findViewWithTag(tag).setTranslationZ(mazo2.size());
                    findViewById(R.id.cl1).findViewWithTag(tag).setX(Mazo2x);
                    findViewById(R.id.cl1).findViewWithTag(tag).setY(LineaY);
                    ImageView cArriba;
                    cArriba = (ImageView) findViewById(R.id.cl1).findViewWithTag("0carta");
                    cArriba.setImageResource(R.drawable.cartavacia);
                }
            }
            Jugadas.pop();
        }else{
            DisplayToast(getApplicationContext(),"No hay jugadas que deshacer");
        }
    }

    /****** Estas funciones toman las string cargadas en CargarPartida() para tranformarlos en Matriz[][], pila o pila<int[]> , según corresponda el caso, es para trabajarlos en cargar la partida**/
    public Carta[][] StringTablero(String s){
        String[] tempTablero = s.split(" ");
        Carta[][] TableroCargado = new Carta[7][7];
        for(int i = 0 ; i < 7 ; i++){
            for(int j = 0 ; j < 7 ; j++){
                if (j <= i) {
                    String tag = tempTablero[j+7*i];
                    int Valor = Integer.parseInt(tag.substring(1,tag.length()));
                    String Pinta = Character.toString(tag.charAt(0));
                    System.out.println(Valor + " " + Pinta);
                    Carta c = new Carta(Valor,Pinta);
                    TableroCargado[j][i] = c;
                    TableroCargado[j][i].setColumna(j);
                    TableroCargado[j][i].setFila(i);
                    TableroCargado[j][i].setEsTablero(true);
                } else {
                    TableroCargado[j][i] = cartaNull;
                }
            }

        }
        ImprimirTablero(TableroCargado);
        return TableroCargado;
    }
    public Stack<Carta> StringStack(String s) {
        Stack<Carta> returnStack = new Stack<>();
        if (s != null) {
            String[] tempStack = s.split(" ");
            for (int i = 0; i < tempStack.length; i++) {
                int Valor = Integer.parseInt(tempStack[i].substring(1, tempStack[i].length()));
                String Pinta = Character.toString(tempStack[i].charAt(0));
                Carta c = new Carta(Valor, Pinta);
                returnStack.push(c);
            }
        }
        return returnStack;
    }
    public Stack<int []> JugadaStack(String s){
        System.out.println(s);
        String[] tempJugada = s.split(",");
        Stack<int[]> JStack = new Stack<>();
        for(int i = 0 ; i < tempJugada.length ; i++){
            String sizes[] = tempJugada[i].split(" ");
            int[] J = {Integer.parseInt(sizes[0]),Integer.parseInt(sizes[1]),Integer.parseInt(sizes[2])};
            JStack.push(J);
        }
        return JStack;
    }

    /*** Función para reiniciar la actividad**/
    public void reiniciarActivity(Activity actividad){
        Intent intent=new Intent();
        intent.setClass(actividad, actividad.getClass());
        intent.putExtra("PartidaGuardada",false);
        intent.putExtra("Carta",cartastring);
        actividad.startActivity(intent);
        actividad.finish();
    }


}


