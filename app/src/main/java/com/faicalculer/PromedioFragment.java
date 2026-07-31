package com.faicalculer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.database.CalculoRepository;
import com.faicalculer.model.Calculo;
import com.faicalculer.model.PromedioRow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PromedioFragment extends Fragment {

    private static final String ARG_CLIENTE = "arg_cliente";

    private TextView tvTituloCliente;
    private TextView tvTbPesos, tvTbLectura, tvMerma;
    private EditText etFundido;
    private RecyclerView rvTable;
    private View cardContainer;
    private ImageView btnBack;
    private View btnAddRow, btnLimpiar, btnImprimir;

    private CalculoRepository repository;
    private PromedioAdapter adapter;
    private String selectedClienteArg = null;
    private List<Calculo> allCalculos = new ArrayList<>();

    public static PromedioFragment newInstance(String nombreCliente) {
        PromedioFragment fragment = new PromedioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CLIENTE, nombreCliente);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedClienteArg = getArguments().getString(ARG_CLIENTE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promedio, container, false);

        tvTituloCliente = view.findViewById(R.id.tv_promedio_titulo_cliente);
        tvTbPesos = view.findViewById(R.id.tv_prom_tb_pesos);
        tvTbLectura = view.findViewById(R.id.tv_prom_tb_lectura);
        etFundido = view.findViewById(R.id.et_prom_fundido);
        tvMerma = view.findViewById(R.id.tv_prom_merma);
        rvTable = view.findViewById(R.id.rv_promedio_table);
        cardContainer = view.findViewById(R.id.card_promedio_container);
        btnBack = view.findViewById(R.id.btn_promedio_back);
        btnAddRow = view.findViewById(R.id.btn_promedio_add_row);
        btnLimpiar = view.findViewById(R.id.btn_promedio_limpiar);
        btnImprimir = view.findViewById(R.id.btn_promedio_imprimir);

        repository = new CalculoRepository(requireContext());
        rvTable.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PromedioAdapter(new PromedioAdapter.OnPromedioDataChangedListener() {
            @Override
            public void onDataChanged() {
                recalcularTotalesExcel();
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

        if (btnAddRow != null) {
            btnAddRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.addRow(0, 0);
                }
            });
        }

        if (btnLimpiar != null) {
            btnLimpiar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clearRows();
                    if (etFundido != null) etFundido.setText("");
                    recalcularTotalesExcel();
                    Toast.makeText(requireContext(), "Tabla limpiada", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (btnImprimir != null) {
            btnImprimir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cardContainer != null) {
                        String clienteStr = selectedClienteArg != null ? selectedClienteArg : "Promedio";
                        BluetoothPrinterHelper.printAcumuladoCard(requireContext(), cardContainer, clienteStr);
                    }
                }
            });
        }

        if (etFundido != null) {
            etFundido.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    recalcularTotalesExcel();
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        loadInitialData();

        return view;
    }

    private void loadInitialData() {
        allCalculos = repository.getAllCalculos();
        List<PromedioRow> initialRows = new ArrayList<>();

        if (selectedClienteArg != null && !selectedClienteArg.trim().isEmpty()) {
            tvTituloCliente.setText("PROMEDIO DE CLIENTE: “" + selectedClienteArg + "”");
            for (Calculo c : allCalculos) {
                if (c.getCliente() != null && c.getCliente().trim().equalsIgnoreCase(selectedClienteArg.trim())) {
                    // Priorizar el PESO BRUTO (pesoSinFundir / pesoMaterial) como indica el Excel del cliente
                    double peso = c.getPesoSinFundir() > 0 ? c.getPesoSinFundir() : (c.getPesoMaterial() > 0 ? c.getPesoMaterial() : c.getPesoFundido());
                    double leyDecimal = c.getLey();
                    if (leyDecimal > 100.0) {
                        leyDecimal = leyDecimal / 1000.0;
                    } else if (leyDecimal > 1.0) {
                        leyDecimal = leyDecimal / 100.0;
                    }
                    initialRows.add(new PromedioRow(initialRows.size() + 1, peso, leyDecimal));
                }
            }
        } else {
            tvTituloCliente.setText("PROMEDIO Y PONDERACIÓN (EXCEL)");
            int limit = Math.min(6, allCalculos.size());
            for (int i = 0; i < limit; i++) {
                Calculo c = allCalculos.get(i);
                double peso = c.getPesoSinFundir() > 0 ? c.getPesoSinFundir() : (c.getPesoMaterial() > 0 ? c.getPesoMaterial() : c.getPesoFundido());
                double leyDecimal = c.getLey();
                if (leyDecimal > 100.0) {
                    leyDecimal = leyDecimal / 1000.0;
                } else if (leyDecimal > 1.0) {
                    leyDecimal = leyDecimal / 100.0;
                }
                initialRows.add(new PromedioRow(i + 1, peso, leyDecimal));
            }
        }

        if (initialRows.isEmpty()) {
            for (int i = 1; i <= 6; i++) {
                initialRows.add(new PromedioRow(i, 0, 0));
            }
        }

        adapter.setRows(initialRows);
        recalcularTotalesExcel();
    }

    /**
     * Aplica la fórmula exacta de la plantilla Excel del cliente (promedios.xlsx):
     * TOTAL PESO BRUTO = =SUMA(PESOS)
     * LECTURA PROMEDIO BRUTO = =SUMAPRODUCTO(PESOS; LECTURA) / TOTAL_BRUTO
     * MERMA = TOTAL_BRUTO - FUNDIDO
     */
    private void recalcularTotalesExcel() {
        List<PromedioRow> rows = adapter.getRows();
        double sumPesosBruto = 0;
        double sumSumaProducto = 0;

        for (PromedioRow r : rows) {
            double p = r.getPeso();
            double l = r.getLectura();
            if (p > 0) {
                sumPesosBruto += p;
                sumSumaProducto += (p * l);
            }
        }

        double lecturaPromedioBruto = sumPesosBruto > 0 ? (sumSumaProducto / sumPesosBruto) : 0;

        DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

        if (tvTbPesos != null) tvTbPesos.setText(dfClean.format(sumPesosBruto) + " g");
        if (tvTbLectura != null) tvTbLectura.setText(String.format(Locale.US, "%.7f", lecturaPromedioBruto).replaceAll("0+$", "").replaceAll("\\.$", ""));

        double pesoFundido = 0;
        if (etFundido != null) {
            String fStr = etFundido.getText().toString().trim();
            if (!fStr.isEmpty()) {
                try {
                    pesoFundido = Double.parseDouble(fStr);
                } catch (Exception ignored) {}
            }
        }

        double merma = (sumPesosBruto > 0 && pesoFundido > 0) ? Math.max(0, sumPesosBruto - pesoFundido) : 0;
        if (tvMerma != null) {
            tvMerma.setText(dfClean.format(merma) + " g");
        }
    }
}
