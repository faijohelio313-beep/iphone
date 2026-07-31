package com.faicalculer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.database.CalculoRepository;
import com.faicalculer.model.Calculo;
import com.faicalculer.model.ClienteAcumulado;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RegistroAcumuladoFragment extends Fragment {

    private RecyclerView rvTable;
    private ImageView btnBack;
    private MaterialButton btnAbrirCalendario, btnVerTodo;
    private TextView tvSubtituloFecha;
    private TextView tvKpiPrecioSoles, tvKpiDescuento, tvKpiPagoNeto;

    private RegistroAcumuladoAdapter adapter;
    private CalculoRepository repository;
    private String selectedDateFilterStr = null; // null = todo el historial

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_acumulado, container, false);

        rvTable = view.findViewById(R.id.rv_acumulado_table);
        btnBack = view.findViewById(R.id.btn_acumulado_back);
        btnAbrirCalendario = view.findViewById(R.id.btn_abrir_calendario);
        btnVerTodo = view.findViewById(R.id.btn_ver_todo);
        tvSubtituloFecha = view.findViewById(R.id.tv_acumulado_fecha_subtitulo);
        tvKpiPrecioSoles = view.findViewById(R.id.tv_acum_kpi_precio_soles);
        tvKpiDescuento = view.findViewById(R.id.tv_acum_kpi_descuento);
        tvKpiPagoNeto = view.findViewById(R.id.tv_acum_kpi_pago_neto);

        repository = new CalculoRepository(requireContext());
        rvTable.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new RegistroAcumuladoAdapter();
        adapter.setListener(new RegistroAcumuladoAdapter.OnAcumuladoChangeListener() {
            @Override
            public void onDataChanged() {
                loadData();
            }
        });
        rvTable.setAdapter(adapter);

        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).selectSubTab("REGISTRO");
                    }
                }
            });
        }

        btnAbrirCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendarioEmergente();
            }
        });

        btnVerTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDateFilterStr = null;
                btnAbrirCalendario.setText("📅 FECHA");
                tvSubtituloFecha.setText("Todo el historial acumulado");
                loadData();
            }
        });

        loadData();

        return view;
    }

    private void mostrarCalendarioEmergente() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dateStr = String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        selectedDateFilterStr = dateStr;
                        btnAbrirCalendario.setText("📅 " + dateStr);
                        tvSubtituloFecha.setText("Resumen del día: " + dateStr);
                        loadData();
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    public void loadData() {
        if (repository == null) return;
        List<Calculo> allCalculos = repository.getAllCalculos();

        List<Calculo> filteredCalculos = new ArrayList<>();
        if (selectedDateFilterStr != null) {
            for (Calculo c : allCalculos) {
                if (c.getFecha() != null && c.getFecha().contains(selectedDateFilterStr)) {
                    filteredCalculos.add(c);
                }
            }
        } else {
            filteredCalculos = allCalculos;
        }

        // Agrupar los cálculos ordenados por cliente respetando orden de aparición
        Map<String, ClienteAcumulado> mapClientes = new LinkedHashMap<>();

        for (Calculo c : filteredCalculos) {
            String nombreCli = c.getCliente();
            if (nombreCli == null || nombreCli.trim().isEmpty()) {
                nombreCli = "X";
            }
            String key = nombreCli.trim().toLowerCase();
            ClienteAcumulado ca = mapClientes.get(key);
            if (ca == null) {
                ca = new ClienteAcumulado(nombreCli.trim());
                mapClientes.put(key, ca);
            }
            ca.addCalculo(c);
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("fajio_desc_univ_prefs", Context.MODE_PRIVATE);

        double sumPrecioSoles = 0;
        double sumDescuento = 0;
        double sumPagoNeto = 0;

        for (Map.Entry<String, ClienteAcumulado> entry : mapClientes.entrySet()) {
            String key = entry.getKey();
            ClienteAcumulado ca = entry.getValue();

            if (prefs.contains("desc_univ_" + key)) {
                float savedDesc = prefs.getFloat("desc_univ_" + key, 0.0f);
                ca.setDescuentoUniversal(savedDesc);
            } else {
                double sumDescLote = 0;
                for (Calculo c : ca.getCalculos()) {
                    sumDescLote += c.getDescuentoMonto();
                }
                ca.setDescuentoUniversal(sumDescLote);
            }

            sumPrecioSoles += ca.getSubtotalPagoTotal();
            sumDescuento += ca.getDescuentoUniversal();
            sumPagoNeto += ca.getPagoTotalFinal();
        }

        DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
        symbolsPE.setGroupingSeparator(',');
        symbolsPE.setDecimalSeparator('.');
        DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);

        if (tvKpiPrecioSoles != null) tvKpiPrecioSoles.setText("S/. " + dfSoles.format(sumPrecioSoles));
        if (tvKpiDescuento != null) tvKpiDescuento.setText("S/. " + dfSoles.format(sumDescuento));
        if (tvKpiPagoNeto != null) tvKpiPagoNeto.setText("S/. " + dfSoles.format(sumPagoNeto));

        List<ClienteAcumulado> listaAcumulada = new ArrayList<>(mapClientes.values());
        adapter.setClientesAcumulados(listaAcumulada);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}
