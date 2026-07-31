package com.faicalculer;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.model.Prestamo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PrestamosAdapter extends RecyclerView.Adapter<PrestamosAdapter.PrestamoViewHolder> {

    public interface OnPrestamoActionListener {
        void onAbonar(Prestamo prestamo);
        void onMarcarPagado(Prestamo prestamo);
        void onEditar(Prestamo prestamo);
        void onEliminar(Prestamo prestamo);
    }

    private List<Prestamo> list = new ArrayList<>();
    private final OnPrestamoActionListener listener;

    public PrestamosAdapter(OnPrestamoActionListener listener) {
        this.listener = listener;
    }

    public void setList(List<Prestamo> newList) {
        this.list = newList != null ? newList : new ArrayList<Prestamo>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PrestamoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prestamo_card, parent, false);
        return new PrestamoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PrestamoViewHolder holder, int position) {
        final Prestamo p = list.get(position);

        DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
        symbolsPE.setGroupingSeparator(',');
        symbolsPE.setDecimalSeparator('.');
        DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);

        holder.tvCliente.setText("Cliente: " + p.getCliente());
        holder.tvMotivo.setText("Motivo: " + (p.getMotivo() != null && !p.getMotivo().isEmpty() ? p.getMotivo() : "Sin motivo"));
        holder.tvFecha.setText(p.getFecha() != null ? p.getFecha() : "");

        holder.tvMonto.setText("S/. " + dfSoles.format(p.getMonto()));
        holder.tvPagado.setText("S/. " + dfSoles.format(p.getMontoPagado()));
        holder.tvSaldo.setText("S/. " + dfSoles.format(p.getSaldoPendiente()));

        String estado = p.getEstado() != null ? p.getEstado().toUpperCase() : "PENDIENTE";
        holder.tvEstado.setText(estado);

        // Cálculo de porcentaje pagado e indicador visual UI/UX Pro Max
        int pctPagado = 0;
        if (p.getMonto() > 0) {
            pctPagado = (int) Math.min(100, Math.max(0, Math.round((p.getMontoPagado() / p.getMonto()) * 100.0)));
        }
        if (holder.pbProgreso != null) {
            holder.pbProgreso.setProgress(pctPagado);
        }
        if (holder.tvPctPagado != null) {
            holder.tvPctPagado.setText(pctPagado + "% Pagado");
        }

        if (estado.equals("PAGADO")) {
            holder.tvEstado.setTextColor(Color.parseColor("#10B981")); // Verde
            holder.tvEstado.setBackgroundColor(Color.parseColor("#064E3B"));
            holder.tvSaldo.setTextColor(Color.parseColor("#10B981"));
            if (holder.tvPctPagado != null) holder.tvPctPagado.setTextColor(Color.parseColor("#10B981"));
        } else if (estado.equals("ADELANTO")) {
            holder.tvEstado.setTextColor(Color.parseColor("#38BDF8")); // Azul
            holder.tvEstado.setBackgroundColor(Color.parseColor("#0C4A6E"));
            holder.tvSaldo.setTextColor(Color.parseColor("#F59E0B"));
            if (holder.tvPctPagado != null) holder.tvPctPagado.setTextColor(Color.parseColor("#38BDF8"));
        } else {
            holder.tvEstado.setTextColor(Color.parseColor("#F59E0B")); // Naranja
            holder.tvEstado.setBackgroundColor(Color.parseColor("#78350F"));
            holder.tvSaldo.setTextColor(Color.parseColor("#EF4444"));
            if (holder.tvPctPagado != null) holder.tvPctPagado.setTextColor(Color.parseColor("#F59E0B"));
        }

        View.OnClickListener onCardClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CustomActionMenuHelper.ActionOption> options = new ArrayList<>();
                options.add(new CustomActionMenuHelper.ActionOption("💵", "Registrar Pago / Adelanto", Color.parseColor("#10B981"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onAbonar(p);
                    }
                }));
                options.add(new CustomActionMenuHelper.ActionOption("✅", "Marcar como PAGADO Total", Color.parseColor("#38BDF8"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onMarcarPagado(p);
                    }
                }));
                options.add(new CustomActionMenuHelper.ActionOption("✏️", "Editar Préstamo", Color.parseColor("#F8FAFC"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onEditar(p);
                    }
                }));
                options.add(new CustomActionMenuHelper.ActionOption("🖨️", "Imprimir Ticket (ADV-8011N)", Color.parseColor("#F59E0B"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BluetoothPrinterHelper.printAcumuladoCard(holder.itemView.getContext(), holder.cardContainer, p.getCliente());
                    }
                }));
                options.add(new CustomActionMenuHelper.ActionOption("🗑️", "Eliminar Registro", Color.parseColor("#EF4444"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) listener.onEliminar(p);
                    }
                }));

                CustomActionMenuHelper.showMenu(holder.itemView.getContext(), "Préstamo: " + p.getCliente(), options);
            }
        };

        holder.itemView.setOnClickListener(onCardClick);
        holder.cardContainer.setOnClickListener(onCardClick);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PrestamoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCliente, tvMotivo, tvFecha, tvEstado, tvMonto, tvPagado, tvSaldo, tvPctPagado;
        android.widget.ProgressBar pbProgreso;
        View cardContainer;

        public PrestamoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCliente = itemView.findViewById(R.id.tv_prestamo_cliente);
            tvMotivo = itemView.findViewById(R.id.tv_prestamo_motivo);
            tvFecha = itemView.findViewById(R.id.tv_prestamo_fecha);
            tvEstado = itemView.findViewById(R.id.tv_prestamo_estado);
            tvMonto = itemView.findViewById(R.id.tv_prestamo_monto);
            tvPagado = itemView.findViewById(R.id.tv_prestamo_pagado);
            tvSaldo = itemView.findViewById(R.id.tv_prestamo_saldo);
            tvPctPagado = itemView.findViewById(R.id.tv_prestamo_pct_pagado);
            pbProgreso = itemView.findViewById(R.id.pb_prestamo_progreso);
            cardContainer = itemView.findViewById(R.id.card_prestamo_container);
        }
    }
}
