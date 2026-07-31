package com.faicalculer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Representa un registro de Precio del Oro extraído de Kitco.
 */
@Entity(tableName = "precio_oro")
public class PrecioOro {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "fecha")
    private String fecha; // Formato YYYY-MM-DD

    @ColumnInfo(name = "valor_onza")
    private double valorOnza;

    @ColumnInfo(name = "valor_gramo")
    private double valorGramo;

    @ColumnInfo(name = "notas")
    private String notas;

    // Constructor vacío
    public PrecioOro() {
    }

    // Constructor completo sin ID (para nuevas inserciones)
    @Ignore
    public PrecioOro(String fecha, double valorOnza, double valorGramo, String notas) {
        this.fecha = fecha;
        this.valorOnza = valorOnza;
        this.valorGramo = valorGramo;
        this.notas = notas;
    }

    // Constructor completo con ID (para registros existentes en BD)
    @Ignore
    public PrecioOro(Long id, String fecha, double valorOnza, double valorGramo, String notas) {
        this.id = id;
        this.fecha = fecha;
        this.valorOnza = valorOnza;
        this.valorGramo = valorGramo;
        this.notas = notas;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getValorOnza() {
        return valorOnza;
    }

    public void setValorOnza(double valorOnza) {
        this.valorOnza = valorOnza;
    }

    public double getValorGramo() {
        return valorGramo;
    }

    public void setValorGramo(double valorGramo) {
        this.valorGramo = valorGramo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    @Override
    public String toString() {
        return "PrecioOro{" +
                "id=" + id +
                ", fecha='" + fecha + '\'' +
                ", valorOnza=" + valorOnza +
                ", valorGramo=" + valorGramo +
                ", notas='" + notas + '\'' +
                '}';
    }
}
