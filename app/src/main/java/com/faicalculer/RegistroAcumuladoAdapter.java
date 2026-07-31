package com.faicalculer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.model.Calculo;
import com.faicalculer.model.ClienteAcumulado;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistroAcumuladoAdapter extends RecyclerView.Adapter<RegistroAcumuladoAdapter.AcumuladoViewHolder> {

    public interface OnAcumuladoChangeListener {
        void onDataChanged();
    }

    private List<ClienteAcumulado> list = new ArrayList<>();
    private OnAcumuladoChangeListener listener;

    public void setListener(OnAcumuladoChangeListener listener) {
        this.listener = listener;
    }

    public void setClientesAcumulados(List<ClienteAcumulado> list) {
        this.list = list != null ? list : new ArrayList<ClienteAcumulado>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AcumuladoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_acumulado_cliente_card, parent, false);
        return new AcumuladoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AcumuladoViewHolder holder, int position) {
        ClienteAcumulado ca = list.get(position);
        holder.bind(ca, listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AcumuladoViewHolder extends RecyclerView.ViewHolder {
        View cardContainer;
        TextView tvClienteNombre, tvCantMateriales, tvFecha;
        LinearLayout llMaterialesContainer;
        TextView tvTotalSinFundir, tvTotalFundido, tvTotalMerma;
        TextView tvSubtotalMateriales, tvDescuentoUniversal, tvPagoTotalFinal;

        public AcumuladoViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.card_acumulado_container);
            tvClienteNombre = itemView.findViewById(R.id.tv_acum_cliente_nombre);
            tvCantMateriales = itemView.findViewById(R.id.tv_acum_cant_materiales);
            tvFecha = itemView.findViewById(R.id.tv_acum_fecha);
            llMaterialesContainer = itemView.findViewById(R.id.ll_acum_materiales_container);
            tvTotalSinFundir = itemView.findViewById(R.id.tv_acum_total_sin_fundir);
            tvTotalFundido = itemView.findViewById(R.id.tv_acum_total_fundido);
            tvTotalMerma = itemView.findViewById(R.id.tv_acum_total_merma);
            tvSubtotalMateriales = itemView.findViewById(R.id.tv_acum_subtotal_materiales);
            tvDescuentoUniversal = itemView.findViewById(R.id.tv_acum_descuento_universal);
            tvPagoTotalFinal = itemView.findViewById(R.id.tv_acum_pago_total_final);
        }

        public void bind(final ClienteAcumulado ca, final OnAcumuladoChangeListener listener) {
            DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
            DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

            tvClienteNombre.setText("“" + ca.getCliente() + "”");
            tvCantMateriales.setText(ca.getCantidadMateriales() + (ca.getCantidadMateriales() == 1 ? " Material" : " Materiales"));
            tvFecha.setText(ca.getFecha());

            // Inflar lista de materiales del cliente
            llMaterialesContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            List<Calculo> calculos = ca.getCalculos();
            for (int i = 0; i < calculos.size(); i++) {
                Calculo c = calculos.get(i);
                View itemMaterial = inflater.inflate(R.layout.item_acumulado_material_subitem, llMaterialesContainer, false);

                TextView tvTitulo = itemMaterial.findViewById(R.id.tv_subitem_titulo);
                TextView tvOnza = itemMaterial.findViewById(R.id.tv_subitem_onza);
                TextView tvLey = itemMaterial.findViewById(R.id.tv_subitem_ley);
                TextView tvDescuento = itemMaterial.findViewById(R.id.tv_subitem_descuento);
                TextView tvTc = itemMaterial.findViewById(R.id.tv_subitem_tc);
                TextView tvSinFundir = itemMaterial.findViewById(R.id.tv_subitem_peso_sin_fundir);
                TextView tvFundido = itemMaterial.findViewById(R.id.tv_subitem_peso_fundido);
                TextView tvMerma = itemMaterial.findViewById(R.id.tv_subitem_merma);
                TextView tvPrecioSoles = itemMaterial.findViewById(R.id.tv_subitem_precio_soles);
                TextView tvGramosFundido = itemMaterial.findViewById(R.id.tv_subitem_gramos_fundido);
                TextView tvPagoTotal = itemMaterial.findViewById(R.id.tv_subitem_pago_total);

                if (tvTitulo != null) tvTitulo.setText("Material #" + (i + 1));
                if (tvOnza != null) tvOnza.setText(dfClean.format(c.getOnza()));
                if (tvLey != null) tvLey.setText(dfClean.format(c.getLey()) + "%");
                if (tvDescuento != null) tvDescuento.setText(dfClean.format(c.getPorcentaje()) + "%");
                if (tvTc != null) tvTc.setText(dfClean.format(c.getTc()));
                if (tvSinFundir != null) tvSinFundir.setText("Sin Fun: " + dfClean.format(c.getPesoSinFundir()) + "g");
                if (tvFundido != null) tvFundido.setText("Fundido: " + dfClean.format(c.getPesoFundido()) + "g");
                if (tvMerma != null) tvMerma.setText("Merma: " + dfClean.format(c.getMerma()) + "g");
                if (tvPrecioSoles != null) tvPrecioSoles.setText("Precio Soles: S/. " + dfSoles.format(c.getPrecioSoles()));
                if (tvGramosFundido != null) tvGramosFundido.setText("Gramos Fundido: " + dfClean.format(c.getPesoFundido()) + "g");
                if (tvPagoTotal != null) tvPagoTotal.setText("Pago Material: S/. " + dfSoles.format(c.getPagoTotal()));

                llMaterialesContainer.addView(itemMaterial);
            }

            // Totales de Resumen Acumulado del Cliente
            tvTotalSinFundir.setText(dfClean.format(ca.getTotalPesoSinFundir()) + " g");
            tvTotalFundido.setText(dfClean.format(ca.getTotalPesoFundido()) + " g");
            tvTotalMerma.setText(dfClean.format(ca.getTotalMerma()) + " g");
            tvSubtotalMateriales.setText("S/. " + dfSoles.format(ca.getSubtotalPagoTotal()));
            tvDescuentoUniversal.setText("- S/. " + dfSoles.format(ca.getDescuentoUniversal()));
            tvPagoTotalFinal.setText("S/. " + dfSoles.format(ca.getPagoTotalFinal()));

            // Menú de opciones al presionar la tarjeta
            View.OnClickListener onCardClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<CustomActionMenuHelper.ActionOption> options = new ArrayList<>();
                    options.add(new CustomActionMenuHelper.ActionOption("👁️", "Vista Previa del Ticket Acumulado", Color.parseColor("#6366F1"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BluetoothPrinterHelper.previewAcumuladoTicket(itemView.getContext(), ca);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("🖨️", "Imprimir Estado Acumulado (Advance ADV-8011N)", Color.parseColor("#F59E0B"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BluetoothPrinterHelper.printAcumuladoCard(itemView.getContext(), cardContainer != null ? cardContainer : itemView, ca);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("✏️", "Aplicar Descuento Universal", Color.parseColor("#F8FAFC"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mostrarDialogoDescuentoUniversal(itemView.getContext(), ca, listener);
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("📊", "Calcular Promedio", Color.parseColor("#38BDF8"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemView.getContext() instanceof MainActivity) {
                                ((MainActivity) itemView.getContext()).abrirPromedioParaCliente(ca.getCliente());
                            }
                        }
                    }));
                    options.add(new CustomActionMenuHelper.ActionOption("🔗", "Compartir Resumen Acumulado", Color.parseColor("#10B981"), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            compartirAcumulado(ca);
                        }
                    }));

                    CustomActionMenuHelper.showMenu(itemView.getContext(), "Acumulado: " + ca.getCliente(), options);
                }
            };

            itemView.setOnClickListener(onCardClick);
            if (cardContainer != null) cardContainer.setOnClickListener(onCardClick);
        }

        private static void mostrarDialogoDescuentoUniversal(final Context context, final ClienteAcumulado ca, final OnAcumuladoChangeListener listener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Descuento Universal para " + ca.getCliente());
            builder.setMessage("Ingrese el monto del descuento global en soles para todo el lote:");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            if (ca.getDescuentoUniversal() > 0) {
                input.setText(String.format(Locale.US, "%.2f", ca.getDescuentoUniversal()));
            } else {
                input.setHint("0.00");
            }
            builder.setView(input);

            builder.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String val = input.getText().toString().trim();
                        double desc = val.isEmpty() ? 0 : Double.parseDouble(val);
                        ca.setDescuentoUniversal(desc);

                        // Persistir permanentemente en SharedPreferences por cliente
                        String key = ca.getCliente().trim().toLowerCase();
                        SharedPreferences prefs = context.getSharedPreferences("fajio_desc_univ_prefs", Context.MODE_PRIVATE);
                        prefs.edit().putFloat("desc_univ_" + key, (float) desc).apply();

                        if (listener != null) listener.onDataChanged();
                        Toast.makeText(context, "Descuento Universal de S/. " + String.format(Locale.US, "%.2f", desc) + " aplicado a " + ca.getCliente(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "Monto no válido", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        }

        private void compartirAcumulado(ClienteAcumulado ca) {
            try {
                DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
                symbolsPE.setGroupingSeparator(',');
                symbolsPE.setDecimalSeparator('.');
                DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);

                StringBuilder sb = new StringBuilder();
                sb.append("INVERSIONES FAJIO - REGISTRO ACUMULADO\n");
                sb.append("Cliente: ").append(ca.getCliente()).append("\n");
                sb.append("Fecha: ").append(ca.getFecha()).append("\n");
                sb.append("Cant. Materiales: ").append(ca.getCantidadMateriales()).append("\n\n");

                List<Calculo> list = ca.getCalculos();
                for (int i = 0; i < list.size(); i++) {
                    Calculo c = list.get(i);
                    sb.append("Mat #").append(i + 1)
                      .append(" | Ley: ").append(String.format(Locale.US, "%.2f%%", c.getLey()))
                      .append(" | Fundido: ").append(String.format(Locale.US, "%.3fg", c.getPesoFundido()))
                      .append(" | Pago: S/. ").append(dfSoles.format(c.getPagoTotal())).append("\n");
                }

                sb.append("\nTotal Sin Fundir: ").append(String.format(Locale.US, "%.3f g", ca.getTotalPesoSinFundir())).append("\n");
                sb.append("Total Fundido: ").append(String.format(Locale.US, "%.3f g", ca.getTotalPesoFundido())).append("\n");
                sb.append("Total Merma: ").append(String.format(Locale.US, "%.3f g", ca.getTotalMerma())).append("\n");
                sb.append("Subtotal: S/. ").append(dfSoles.format(ca.getSubtotalPagoTotal())).append("\n");
                sb.append("Descuento Universal: - S/. ").append(dfSoles.format(ca.getDescuentoUniversal())).append("\n");
                sb.append("PAGO TOTAL FINAL: S/. ").append(dfSoles.format(ca.getPagoTotalFinal()));

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                itemView.getContext().startActivity(Intent.createChooser(intent, "Compartir Acumulado de " + ca.getCliente()));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Error al compartir acumulado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
