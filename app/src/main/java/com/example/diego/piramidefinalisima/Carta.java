package com.example.diego.piramidefinalisima;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

public class Carta extends Activity{
    int Valor;
    String Pinta;
    Boolean esTablero;
    int Fila;
    int Columna;
    float posx;
    float posy;
    float width;
    float height;

    public Boolean equals(Carta c){
        return ( Valor == c.getValor() )&&( Pinta.equals(c.getPinta() )&&( esTablero == c.getEsTablero() )&&( Fila == c.getFila() )&&( Columna==c.getColumna() )&&( posx==c.getPosx() )&&( posy==c.getPosy() )&&( width == c.getWidth() )&&( height == c.getHeight() ));
    }


    public void setXY_WH(float CoordenadaX, float CoordenadaY, float Ancho, float Alto){
        posx = CoordenadaX;
        posy = CoordenadaY;
        width = Ancho;
        height = Alto;
    }

    public float getPosx() {
        return posx;
    }

    public void setPosx(float posx) {
        this.posx = posx;
    }

    public float getPosy() {
        return posy;
    }

    public void setPosy(float posy) {
        this.posy = posy;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }


    public Carta(){
        Valor = 0;
        Pinta = "N";
        Fila = -1;
        Columna = -1;
        esTablero = false;
        posx = 0;
        posy = 0;
        width = 0;
        height = 0;
    }

    public Carta(int v, String p){
        Valor = v;
        Pinta = p;
        Columna = -1;
        Fila = -1;
        esTablero = false;
        posx = 0;
        posy = 0;
        width = 0;
        height = 0;
    }

    public Carta(int v, String p, int f, int c){
        Valor = v;
        Pinta = p;
        Columna = c;
        Fila = f;
        esTablero = true;
        posx = 0;
        posy = 0;
        width = 0;
        height = 0;
    }

    public Carta(Carta c){
        Valor = c.getValor();
        Pinta = c.getPinta();
        Columna = c.getColumna();
        Fila = c.getFila();
        esTablero = c.getEsTablero();
        posx = c.getPosx();
        posy = c.getPosy();
        width = c.getWidth();
        height = c.getHeight();
    }

    public Boolean getEsTablero() {
        return esTablero;
    }

    public void setEsTablero(Boolean esTablero) {
        this.esTablero = esTablero;
    }

    public int getFila() {
        return Fila;
    }

    public void setFila(int fila) {
        Fila = fila;
    }

    public int getColumna() {
        return Columna;
    }

    public void setColumna(int columna) {
        Columna = columna;
    }

    public String getPinta() {
        return Pinta;
    }

    public void setPinta(String pinta) {
        Pinta = pinta;
    }

    public int getValor() {
        return Valor;
    }

    public void setValor(int valor) {
        if(1<=valor && valor<=13){
            Valor = valor;
        }else {
            Valor = 0;
        }
    }

    public int Suma(Carta c2) {
        return Valor + c2.getValor();
    }

    public String getString(){
        String v = Integer.toString(Valor);
        return Pinta+v;
    }
}