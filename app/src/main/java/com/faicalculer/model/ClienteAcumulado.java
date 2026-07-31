package com.faicalculer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Agrupa los cálculos pertenecientes a un mismo cliente en Registro Acumulado.
 */
public class ClienteAcumulado {
    private String cliente;
    private String fecha;
    private List<Calculo> calculos = new ArrayList<>();
    private double descuentoUniversal = 0.0;

    public ClienteAcumulado(String cliente) {
        this.cliente = cliente != null ? cliente : "X";
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFecha() {
        if (fecha != null && !fecha.isEmpty()) return fecha;
        if (!calculos.isEmpty()) return calculos.get(0).getFecha();
        return "--/--/----";
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Calculo> getCalculos() {
        return calculos;
    }

    public void addCalculo(Calculo c) {
        if (c != null) {
            this.calculos.add(c);
        }
    }

    public int getCantidadMateriales() {
        return calculos.size();
    }

    public double getTotalPesoSinFundir() {
        double sum = 0;
        for (Calculo c : calculos) {
            sum += c.getPesoSinFundir();
        }
        return sum;
    }

    public double getTotalPesoFundido() {
        double sum = 0;
        for (Calculo c : calculos) {
            sum += c.getPesoFundido();
        }
        return sum;
    }

    public double getTotalMerma() {
        double sum = 0;
        for (Calculo c : calculos) {
            sum += c.getMerma();
        }
        return sum;
    }

    public double getSubtotalPrecioTotal() {
        double sum = 0;
        for (Calculo c : calculos) {
            sum += c.getPrecioTotal();
        }
        return sum;
    }

    public double getSubtotalPagoTotal() {
        double sum = 0;
        for (Calculo c : calculos) {
            sum += c.getPagoTotal();
        }
        return sum;
    }

    public double getDescuentoUniversal() {
        return descuentoUniversal;
    }

    public void setDescuentoUniversal(double descuentoUniversal) {
        this.descuentoUniversal = descuentoUniversal;
    }

    public double getPagoTotalFinal() {
        double total = getSubtotalPagoTotal() - descuentoUniversal;
        return Math.max(0, Math.floor(total * 100.0) / 100.0);
    }
}
