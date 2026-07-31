package com.faicalculer.model;

public class PromedioRow {
    private int cantidad;
    private double peso;
    private double lectura;

    public PromedioRow(int cantidad, double peso, double lectura) {
        this.cantidad = cantidad;
        this.peso = peso;
        this.lectura = lectura;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getLectura() {
        return lectura;
    }

    public void setLectura(double lectura) {
        this.lectura = lectura;
    }
}
