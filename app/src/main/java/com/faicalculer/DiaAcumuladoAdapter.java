package com.faicalculer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.model.ClienteAcumulado;
import com.faicalculer.model.DiaAcumulado;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaAcumuladoAdapter extends RecyclerView.Adapter<DiaAcumuladoAdapter.DiaViewHolder> {

    private List<DiaAcumulado> dias = new ArrayList<>();
    private RegistroAcumuladoAdapter.OnAcumuladoChangeListener listener;

    public void setDias(List<DiaAcumulado> newDias, RegistroAcumuladoAdapter.OnAcumuladoChangeListener listener) {
        this.dias = newDias != null ? newDias : new ArrayList<DiaAcumulado>();
        this.listener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acumulado_dia_group, parent, false);
        return new DiaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder holder, int position) {
        DiaAcumulado dia = dias.get(position);

        DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
        symbolsPE.setGroupingSeparator(',');
        symbolsPE.setDecimalSeparator('.');
        DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
        DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

        holder.tvFecha.setText("📆 " + dia.getFecha());
        holder.tvPesoTotal.setText("Peso: " + dfClean.format(dia.getTotalPeso()) + " g");
        holder.tvPrecioSoles.setText("Precio: S/. " + dfSoles.format(dia.getTotalPrecioSoles()));
        holder.tvDescuento.setText("Desc: S/. " + dfSoles.format(dia.getTotalDescuento()));
        holder.tvPagoNeto.setText("Neto: S/. " + dfSoles.format(dia.getTotalPagoNeto()));

        // Inflar dinámicamente las tarjetas de clientes de este día
        holder.containerClientes.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());

        RegistroAcumuladoAdapter subAdapter = new RegistroAcumuladoAdapter();
        subAdapter.setListener(listener);
        subAdapter.setClientesAcumulados(dia.getClientes());

        for (int i = 0; i < subAdapter.getItemCount(); i++) {
            RegistroAcumuladoAdapter.AcumuladoViewHolder childVH = subAdapter.onCreateViewHolder(holder.containerClientes, subAdapter.getItemViewType(i));
            subAdapter.onBindViewHolder(childVH, i);
            holder.containerClientes.addView(childVH.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    static class DiaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvPesoTotal, tvPrecioSoles, tvDescuento, tvPagoNeto;
        LinearLayout containerClientes;

        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tv_dia_fecha_header);
            tvPesoTotal = itemView.findViewById(R.id.tv_dia_peso_total);
            tvPrecioSoles = itemView.findViewById(R.id.tv_dia_precio_soles);
            tvDescuento = itemView.findViewById(R.id.tv_dia_descuento);
            tvPagoNeto = itemView.findViewById(R.id.tv_dia_pago_neto);
            containerClientes = itemView.findViewById(R.id.container_dia_clientes);
        }
    }
}
