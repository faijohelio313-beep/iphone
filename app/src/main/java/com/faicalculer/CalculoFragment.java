package com.faicalculer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.database.CalculoRepository;
import com.faicalculer.model.Calculo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragmento que muestra el historial de cálculos de precio del oro guardados.
 * Permite editar o eliminar registros directamente desde la lista.
 */
public class CalculoFragment extends Fragment {

    private RecyclerView rvCalculos;
    private LinearLayout layoutEmptyState;
    private CalculoAdapter adapter;
    private CalculoRepository repository;
    private List<Calculo> calculoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculo, container, false);

        rvCalculos = view.findViewById(R.id.rv_calculos);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);

        repository = new CalculoRepository(requireContext());

        rvCalculos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CalculoAdapter(calculoList, new OnCalculoItemClickListener() {
            @Override
            public void onEditClick(Calculo calculo) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showCalculoDialog(calculo);
                }
            }

            @Override
            public void onDeleteClick(Calculo calculo) {
                if (calculo.getId() != null) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
                    builder.setTitle("Eliminar registro");
                    builder.setMessage("¿Estás seguro de que deseas eliminar este cálculo?");
                    builder.setPositiveButton("Sí, eliminar", (dialog, which) -> {
                        boolean deleted = repository.delete(calculo.getId());
                        if (deleted) {
                            refreshData();
                            android.widget.Toast.makeText(getContext(), "Registro eliminado", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    builder.show();
                }
            }
        });
        rvCalculos.setAdapter(adapter);

        refreshData();

        return view;
    }

    /**
     * Recarga los datos desde la base de datos y actualiza el RecyclerView.
     */
    public void refreshData() {
        if (repository != null) {
            calculoList.clear();
            calculoList.addAll(repository.getAllCalculos());
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            if (calculoList.isEmpty()) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                rvCalculos.setVisibility(View.GONE);
            } else {
                layoutEmptyState.setVisibility(View.GONE);
                rvCalculos.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Interface para eventos de click sobre los items de la lista.
     */
    public interface OnCalculoItemClickListener {
        void onEditClick(Calculo calculo);
        void onDeleteClick(Calculo calculo);
    }

    /**
     * Adaptador para el RecyclerView de cálculos.
     */
    private static class CalculoAdapter extends RecyclerView.Adapter<CalculoAdapter.CalculoViewHolder> {

        private final List<Calculo> list;
        private final OnCalculoItemClickListener listener;

        public CalculoAdapter(List<Calculo> list, OnCalculoItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public CalculoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calculo, parent, false);
            return new CalculoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CalculoViewHolder holder, int position) {
            Calculo c = list.get(position);
            holder.bind(c, listener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class CalculoViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvFecha;
            private final TextView tvOnza;
            private final TextView tvLey;
            private final TextView tvPorcentaje;
            private final TextView tvTc;
            private final TextView tvPrecioUsd;
            private final TextView tvPrecioSoles;
            private final TextView tvNotas;
            private final ImageView btnEdit;
            private final ImageView btnDelete;

            public CalculoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFecha = itemView.findViewById(R.id.tv_item_fecha);
                tvOnza = itemView.findViewById(R.id.tv_item_onza);
                tvLey = itemView.findViewById(R.id.tv_item_ley);
                tvPorcentaje = itemView.findViewById(R.id.tv_item_porcentaje);
                tvTc = itemView.findViewById(R.id.tv_item_tc);
                tvPrecioUsd = itemView.findViewById(R.id.tv_item_precio_usd);
                tvPrecioSoles = itemView.findViewById(R.id.tv_item_precio_soles);
                tvNotas = itemView.findViewById(R.id.tv_item_notas);
                btnEdit = itemView.findViewById(R.id.btn_item_edit);
                btnDelete = itemView.findViewById(R.id.btn_item_delete);
            }

            public void bind(Calculo c, OnCalculoItemClickListener listener) {
                tvFecha.setText(c.getFecha());
                tvOnza.setText(String.format(Locale.US, "%.1f", c.getOnza()));
                tvLey.setText(String.format(Locale.US, "%.1f%%", c.getLey()));
                tvPorcentaje.setText(String.format(Locale.US, "%.1f%%", c.getPorcentaje()));
                tvTc.setText(String.format(Locale.US, "%.2f", c.getTc()));

                tvPrecioUsd.setText(String.format(Locale.US, "$$ %.2f", c.getPrecioUsd()));
                tvPrecioSoles.setText(String.format(Locale.US, "S/. %.2f", c.getPrecioSoles()));

                if (c.getNotas() != null && !c.getNotas().trim().isEmpty()) {
                    tvNotas.setText(c.getNotas());
                    tvNotas.setVisibility(View.VISIBLE);
                } else {
                    tvNotas.setVisibility(View.GONE);
                }

                btnEdit.setOnClickListener(v -> listener.onEditClick(c));
                btnDelete.setOnClickListener(v -> listener.onDeleteClick(c));
            }
        }
    }
}
