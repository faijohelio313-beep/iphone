package com.faicalculer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.database.CalculoRepository;
import com.faicalculer.model.Calculo;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RegistroFragment extends Fragment {

    private RecyclerView rvCards;
    private MaterialButton btnNuevoCalculo;
    private RegistroAdapter adapter;
    private CalculoRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        rvCards = view.findViewById(R.id.rv_registro_cards);
        btnNuevoCalculo = view.findViewById(R.id.btn_nuevo_calculo_action);

        repository = new CalculoRepository(requireContext());

        rvCards.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RegistroAdapter(new RegistroAdapter.OnRegistroClickListener() {
            @Override
            public void onEdit(Calculo calculo) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showCalculoFormDialog(calculo);
                }
            }

            @Override
            public void onDelete(Calculo calculo) {
                if (calculo.getId() != null) {
                    repository.delete(calculo.getId());
                    loadData();
                    Toast.makeText(requireContext(), "Registro eliminado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCalcularPromedio(Calculo calculo) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).abrirPromedioParaCliente(calculo.getCliente());
                }
            }
        });
        rvCards.setAdapter(adapter);

        btnNuevoCalculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showCalculoFormDialog(null);
                }
            }
        });

        loadData();

        return view;
    }

    public void loadData() {
        if (repository != null && adapter != null) {
            List<Calculo> list = repository.getAllCalculos();
            // Si la lista está vacía, agregar un registro demostrativo predeterminado para que la vista coincida con la Pantalla 1
            if (list.isEmpty()) {
                Calculo demo = new Calculo();
                demo.setCliente("x");
                demo.setFecha("--/--/----");
                demo.setOnza(0);
                demo.setLey(0);
                demo.setPorcentaje(0);
                demo.setTc(0);
                demo.setPrecioUsd(0);
                demo.setPrecioSoles(0);
                demo.setPesoMaterial(0);
                demo.setPrecioTotal(0);
                demo.setDescuentoMotivo("Motivo");
                demo.setDescuentoMonto(0);
                demo.setPagoTotal(0);
                list.add(demo);
            }
            adapter.setCalculos(list);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
