package com.faicalculer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Representa un registro de Tipo de Cambio (Dólar/Sol) extraído de Bloomberg.
 */
@Entity(tableName = "tipo_cambio")
public class TipoCambio {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "fecha")
    private String fecha; // Formato YYYY-MM-DD

    @ColumnInfo(name = "precio_compra")
    private double precioCompra;

    @ColumnInfo(name = "precio_venta")
    private double precioVenta;

    @ColumnInfo(name = "fuente")
    private String fuente;

    // Constructor vacío
    public TipoCambio() {
    }

    // Constructor completo sin ID (para nuevas inserciones)
    @Ignore
    public TipoCambio(String fecha, double precioCompra, double precioVenta, String fuente) {
        this.fecha = fecha;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.fuente = fuente;
    }

    // Constructor completo con ID (para registros existentes en BD)
    @Ignore
    public TipoCambio(Long id, String fecha, double precioCompra, double precioVenta, String fuente) {
        this.id = id;
        this.fecha = fecha;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.fuente = fuente;
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

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    @Override
    public String toString() {
        return "TipoCambio{" +
                "id=" + id +
                ", fecha='" + fecha + '\'' +
                ", precioCompra=" + precioCompra +
                ", precioVenta=" + precioVenta +
                ", fuente='" + fuente + '\'' +
                '}';
    }
}
