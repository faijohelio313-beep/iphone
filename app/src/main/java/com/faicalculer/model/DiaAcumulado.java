package com.faicalculer.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiaAcumulado {
    private String fecha;
    private List<ClienteAcumulado> clientes = new ArrayList<>();
    private double totalPrecioSoles;
    private double totalDescuento;
    private double totalPagoNeto;
    private double totalPeso;

    public DiaAcumulado(String fecha) {
        this.fecha = fecha;
    }

    public void addCalculo(Calculo c) {
        String cliName = c.getCliente();
        if (cliName == null || cliName.trim().isEmpty()) {
            cliName = "Cliente";
        }
        cliName = cliName.trim();

        ClienteAcumulado target = null;
        for (ClienteAcumulado ca : clientes) {
            if (ca.getCliente().equalsIgnoreCase(cliName)) {
                target = ca;
                break;
            }
        }
        if (target == null) {
            target = new ClienteAcumulado(cliName);
            clientes.add(target);
        }
        target.addCalculo(c);

        totalPrecioSoles += c.getPrecioSoles();
        totalDescuento += c.getDescuentoMonto();
        totalPagoNeto += c.getPagoTotal();
        totalPeso += (c.getPesoFundido() > 0 ? c.getPesoFundido() : c.getPesoSinFundir());
    }

    public String getFecha() {
        return fecha;
    }

    public List<ClienteAcumulado> getClientes() {
        return clientes;
    }

    public double getTotalPrecioSoles() {
        return totalPrecioSoles;
    }

    public double getTotalDescuento() {
        return totalDescuento;
    }

    public double getTotalPagoNeto() {
        return totalPagoNeto;
    }

    public double getTotalPeso() {
        return totalPeso;
    }
}
