package com.faicalculer;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.model.Calculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    public interface OnRegistroClickListener {
        void onEdit(Calculo calculo);
        void onDelete(Calculo calculo);
        void onCalcularPromedio(Calculo calculo);
    }

    private List<Calculo> list = new ArrayList<>();
    private final OnRegistroClickListener listener;

    public RegistroAdapter(OnRegistroClickListener listener) {
        this.listener = listener;
    }

    public void setCalculos(List<Calculo> calculos) {
        this.list = calculos != null ? calculos : new ArrayList<Calculo>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_card, parent, false);
        return new RegistroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        Calculo c = list.get(position);
        holder.bind(c, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class RegistroViewHolder extends RecyclerView.ViewHolder {
        View cardContainer;
        TextView tvCliente, tvFecha, tvOnza, tvDescuento, tvTc;
        TextView tvPrecioSoles, tvPesoSinFundir, tvPesoFundido, tvMerma, tvPrecioTotal, tvCantidadFundido;
        TextView tvPagoTotal;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.card_registro_container);
            tvCliente = itemView.findViewById(R.id.tv_card_cliente);
            tvFecha = itemView.findViewById(R.id.tv_card_fecha);
            tvOnza = itemView.findViewById(R.id.tv_card_onza);
            tvDescuento = itemView.findViewById(R.id.tv_card_descuento);
            tvTc = itemView.findViewById(R.id.tv_card_tc);
            tvPrecioSoles = itemView.findViewById(R.id.tv_card_precio_soles);
            tvPesoSinFundir = itemView.findViewById(R.id.tv_card_peso_sin_fundir);
            tvPesoFundido = itemView.findViewById(R.id.tv_card_peso_fundido);
            tvMerma = itemView.findViewById(R.id.tv_card_merma);
            tvPrecioTotal = itemView.findViewById(R.id.tv_card_precio_total);
            tvCantidadFundido = itemView.findViewById(R.id.tv_card_cantidad_fundido);
            tvPagoTotal = itemView.findViewById(R.id.tv_card_pago_total);
        }

        public void bind(final Calculo c, final OnRegistroClickListener listener) {
            java.text.DecimalFormatSymbols symbolsPE = new java.text.DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            java.text.DecimalFormat dfSoles = new java.text.DecimalFormat("#,##0.00", symbolsPE);

            java.text.DecimalFormat dfClean = new java.text.DecimalFormat("0.###", new java.text.DecimalFormatSymbols(Locale.US));

            tvCliente.setText("“" + c.getCliente() + "”");
            tvFecha.setText(c.getFecha());
            tvOnza.setText(dfClean.format(c.getOnza()));
            tvDescuento.setText(dfClean.format(c.getPorcentaje()));
            tvTc.setText(dfClean.format(c.getTc()));
            tvPrecioSoles.setText("S/. " + dfSoles.format(c.getPrecioSoles()));

            tvPesoSinFundir.setText(dfClean.format(c.getPesoSinFundir()) + " g");
            tvPesoFundido.setText(dfClean.format(c.getPesoFundido()) + " g");
            tvMerma.setText(dfClean.format(c.getMerma()) + " g");
            tvPrecioTotal.setText("S/. " + dfSoles.format(c.getPrecioTotal()));
            if (tvCantidadFundido != null) {
                tvCantidadFundido.setText(dfClean.format(c.getPesoFundido()) + " g");
            }
            tvPagoTotal.setText("S/. " + dfSoles.format(c.getPagoTotal()));

            // Al presionar cualquier parte de la tarjeta limpia se despliega el menú de acciones Premium
            View.OnClickListener onCardClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<CustomActionMenuHelper.ActionOption> options = new ArrayList<>();
                    options.add(new CustomActionMenuHelper.ActionOption("✏️", "Editar Registro", android.graphics.Color.parseColor("#F8FAFC"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) listener.onEdit(c);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("👁️", "Vista Previa del Ticket", android.graphics.Color.parseColor("#6366F1"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BluetoothPrinterHelper.previewCalculoTicket(itemView.getContext(), c);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("🖨️", "Imprimir Ticket (Advance ADV-8011N)", android.graphics.Color.parseColor("#F59E0B"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final View targetView = cardContainer != null ? cardContainer : itemView;
                            BluetoothPrinterHelper.printCalculoTicket(itemView.getContext(), targetView, c);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("📊", "Calcular Promedio", android.graphics.Color.parseColor("#38BDF8"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) listener.onCalcularPromedio(c);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("🔗", "Compartir Recibo", android.graphics.Color.parseColor("#10B981"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            compartirRegistro(c);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("🗑️", "Eliminar Registro", android.graphics.Color.parseColor("#EF4444"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (listener != null) listener.onDelete(c);
                        }
                    }));

                    CustomActionMenuHelper.showMenu(itemView.getContext(), "Acciones: " + c.getCliente(), options);
                }
            };

            itemView.setOnClickListener(onCardClick);
            if (cardContainer != null) cardContainer.setOnClickListener(onCardClick);
        }

        private void compartirRegistro(Calculo c) {
            try {
                String text = "INVERSIONES FAJIO\n" +
                        "Cliente: " + c.getCliente() + "\n" +
                        "Fecha: " + c.getFecha() + "\n" +
                        "Pago Total: S/. " + String.format(Locale.US, "%.2f", c.getPagoTotal());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                itemView.getContext().startActivity(Intent.createChooser(intent, "Compartir Recibo de " + c.getCliente()));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Error al compartir: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
