package com.faicalculer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Representa un registro de cálculo en Inversiones Fajio.
 */
@Entity(tableName = "calculo")
public class Calculo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "cliente")
    private String cliente; // ej. "Paul"

    @ColumnInfo(name = "fecha")
    private String fecha; // ej. "--/--/----" o "2026-07-24"

    @ColumnInfo(name = "onza")
    private double onza;

    @ColumnInfo(name = "ley")
    private double ley;

    @ColumnInfo(name = "porcentaje")
    private double porcentaje; // Descuento %

    @ColumnInfo(name = "tc")
    private double tc; // Tipo de Cambio

    @ColumnInfo(name = "precio_usd")
    private double precioUsd;

    @ColumnInfo(name = "precio_soles")
    private double precioSoles;

    @ColumnInfo(name = "peso_material")
    private double pesoMaterial;

    @ColumnInfo(name = "peso_sin_fundir")
    private double pesoSinFundir;

    @ColumnInfo(name = "peso_fundido")
    private double pesoFundido;

    @ColumnInfo(name = "merma")
    private double merma;

    @ColumnInfo(name = "precio_total")
    private double precioTotal;

    @ColumnInfo(name = "descuento_motivo")
    private String descuentoMotivo;

    @ColumnInfo(name = "descuento_monto")
    private double descuentoMonto;

    @ColumnInfo(name = "pago_total")
    private double pagoTotal;

    @ColumnInfo(name = "notas")
    private String notas;

    public Calculo() {
        this.cliente = "x";
        this.fecha = "--/--/----";
        this.descuentoMotivo = "";
    }

    @Ignore
    public Calculo(Long id, String cliente, String fecha, double onza, double ley, double porcentaje, double tc, double precioUsd, double precioSoles, double pesoMaterial, double pesoSinFundir, double pesoFundido, double merma, double precioTotal, String descuentoMotivo, double descuentoMonto, double pagoTotal, String notas) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.onza = onza;
        this.ley = ley;
        this.porcentaje = porcentaje;
        this.tc = tc;
        this.precioUsd = precioUsd;
        this.precioSoles = precioSoles;
        this.pesoMaterial = pesoMaterial;
        this.pesoSinFundir = pesoSinFundir;
        this.pesoFundido = pesoFundido;
        this.merma = merma;
        this.precioTotal = precioTotal;
        this.descuentoMotivo = descuentoMotivo;
        this.descuentoMonto = descuentoMonto;
        this.pagoTotal = pagoTotal;
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente != null && !cliente.isEmpty() ? cliente : "x";
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFecha() {
        return fecha != null && !fecha.isEmpty() ? fecha : "--/--/----";
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getOnza() {
        return onza;
    }

    public void setOnza(double onza) {
        this.onza = onza;
    }

    public double getLey() {
        return ley;
    }

    public void setLey(double ley) {
        this.ley = ley;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public double getTc() {
        return tc;
    }

    public void setTc(double tc) {
        this.tc = tc;
    }

    public double getPrecioUsd() {
        return precioUsd;
    }

    public void setPrecioUsd(double precioUsd) {
        this.precioUsd = precioUsd;
    }

    public double getPrecioSoles() {
        return precioSoles;
    }

    public void setPrecioSoles(double precioSoles) {
        this.precioSoles = precioSoles;
    }

    public double getPesoMaterial() {
        return pesoMaterial;
    }

    public void setPesoMaterial(double pesoMaterial) {
        this.pesoMaterial = pesoMaterial;
    }

    public double getPesoSinFundir() {
        return pesoSinFundir > 0 ? pesoSinFundir : pesoMaterial;
    }

    public void setPesoSinFundir(double pesoSinFundir) {
        this.pesoSinFundir = pesoSinFundir;
    }

    public double getPesoFundido() {
        return pesoFundido > 0 ? pesoFundido : pesoMaterial;
    }

    public void setPesoFundido(double pesoFundido) {
        this.pesoFundido = pesoFundido;
    }

    public double getMerma() {
        return merma;
    }

    public void setMerma(double merma) {
        this.merma = merma;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getDescuentoMotivo() {
        return descuentoMotivo != null ? descuentoMotivo : "";
    }

    public void setDescuentoMotivo(String descuentoMotivo) {
        this.descuentoMotivo = descuentoMotivo;
    }

    public double getDescuentoMonto() {
        return descuentoMonto;
    }

    public void setDescuentoMonto(double descuentoMonto) {
        this.descuentoMonto = descuentoMonto;
    }

    public double getPagoTotal() {
        return pagoTotal;
    }

    public void setPagoTotal(double pagoTotal) {
        this.pagoTotal = pagoTotal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
