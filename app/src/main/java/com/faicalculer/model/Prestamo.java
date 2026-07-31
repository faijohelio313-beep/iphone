package com.faicalculer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "prestamo")
public class Prestamo implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "cliente")
    private String cliente;

    @ColumnInfo(name = "motivo")
    private String motivo;

    @ColumnInfo(name = "monto")
    private double monto;

    @ColumnInfo(name = "monto_pagado")
    private double montoPagado;

    @ColumnInfo(name = "fecha")
    private String fecha;

    @ColumnInfo(name = "estado")
    private String estado; // "PENDIENTE", "ADELANTO", "PAGADO"

    @ColumnInfo(name = "notas")
    private String notas;

    public Prestamo() {
        this.estado = "PENDIENTE";
        this.montoPagado = 0;
    }

    @Ignore
    public Prestamo(Long id, String cliente, String motivo, double monto, double montoPagado, String fecha, String estado, String notas) {
        this.id = id;
        this.cliente = cliente;
        this.motivo = motivo;
        this.monto = monto;
        this.montoPagado = montoPagado;
        this.fecha = fecha;
        this.estado = estado != null ? estado : "PENDIENTE";
        this.notas = notas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public double getSaldoPendiente() {
        return Math.max(0, monto - montoPagado);
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
