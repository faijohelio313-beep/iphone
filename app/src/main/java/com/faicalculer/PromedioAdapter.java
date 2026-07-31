package com.faicalculer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.model.PromedioRow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PromedioAdapter extends RecyclerView.Adapter<PromedioAdapter.PromedioViewHolder> {

    public interface OnPromedioDataChangedListener {
        void onDataChanged();
    }

    private List<PromedioRow> rows = new ArrayList<>();
    private OnPromedioDataChangedListener dataChangedListener;

    public PromedioAdapter(OnPromedioDataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    public void setRows(List<PromedioRow> newRows) {
        this.rows = newRows != null ? newRows : new ArrayList<PromedioRow>();
        reindexRows();
        notifyDataSetChanged();
    }

    public List<PromedioRow> getRows() {
        return rows;
    }

    public void addRow(double peso, double lectura) {
        rows.add(new PromedioRow(rows.size() + 1, peso, lectura));
        notifyItemInserted(rows.size() - 1);
        if (dataChangedListener != null) dataChangedListener.onDataChanged();
    }

    public void removeRow(int position) {
        if (position >= 0 && position < rows.size()) {
            rows.remove(position);
            reindexRows();
            notifyDataSetChanged();
            if (dataChangedListener != null) dataChangedListener.onDataChanged();
        }
    }

    public void clearRows() {
        rows.clear();
        notifyDataSetChanged();
        if (dataChangedListener != null) dataChangedListener.onDataChanged();
    }

    private void reindexRows() {
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setCantidad(i + 1);
        }
    }

    @NonNull
    @Override
    public PromedioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promedio_row, parent, false);
        return new PromedioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final PromedioViewHolder holder, int position) {
        final PromedioRow row = rows.get(position);

        DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

        holder.tvCantidad.setText(String.valueOf(row.getCantidad()));

        // Remover TextWatchers anteriores para evitar ciclos y vaciados en RecyclerView
        if (holder.pesosWatcher != null) {
            holder.etPesos.removeTextChangedListener(holder.pesosWatcher);
        }
        if (holder.lecturaWatcher != null) {
            holder.etLectura.removeTextChangedListener(holder.lecturaWatcher);
        }

        // Asignar texto de peso de forma segura
        holder.etPesos.setText(row.getPeso() > 0 ? dfClean.format(row.getPeso()) : "");

        // Asignar texto de lectura con precisión completa de hasta 7 decimales
        double lec = row.getLectura();
        if (lec > 0) {
            if (lec > 100.0) {
                holder.etLectura.setText(String.format(Locale.US, "%.7f", lec / 1000.0).replaceAll("0+$", "").replaceAll("\\.$", ""));
            } else if (lec > 1.0) {
                holder.etLectura.setText(String.format(Locale.US, "%.7f", lec / 100.0).replaceAll("0+$", "").replaceAll("\\.$", ""));
            } else {
                holder.etLectura.setText(String.format(Locale.US, "%.7f", lec).replaceAll("0+$", "").replaceAll("\\.$", ""));
            }
        } else {
            holder.etLectura.setText("");
        }

        // Crear nuevos TextWatchers desacoplados de la asignación inicial de texto
        holder.pesosWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double val = parseDouble(s.toString());
                    row.setPeso(val);
                    if (dataChangedListener != null) dataChangedListener.onDataChanged();
                } catch (Exception ignored) {}
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.lecturaWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double val = parseDouble(s.toString());
                    if (val > 100.0) {
                        val = val / 1000.0;
                    } else if (val > 1.0 && val <= 100.0) {
                        val = val / 100.0;
                    }
                    row.setLectura(val);
                    if (dataChangedListener != null) dataChangedListener.onDataChanged();
                } catch (Exception ignored) {}
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.etPesos.addTextChangedListener(holder.pesosWatcher);
        holder.etLectura.addTextChangedListener(holder.lecturaWatcher);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    removeRow(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    private double parseDouble(String val) {
        if (val == null) return 0;
        String sanitized = val.replaceAll("[^0-9.]", "").trim();
        if (sanitized.isEmpty()) return 0;
        try {
            return Double.parseDouble(sanitized);
        } catch (Exception e) {
            return 0;
        }
    }

    static class PromedioViewHolder extends RecyclerView.ViewHolder {
        TextView tvCantidad;
        EditText etPesos, etLectura;
        ImageView btnDelete;
        TextWatcher pesosWatcher, lecturaWatcher;

        public PromedioViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.tv_prom_cantidad);
            etPesos = itemView.findViewById(R.id.et_prom_pesos);
            etLectura = itemView.findViewById(R.id.et_prom_lectura);
            btnDelete = itemView.findViewById(R.id.btn_prom_delete_row);
        }
    }
}
