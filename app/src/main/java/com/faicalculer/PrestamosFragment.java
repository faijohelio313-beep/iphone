package com.faicalculer;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faicalculer.database.PrestamoRepository;
import com.faicalculer.model.Prestamo;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrestamosFragment extends Fragment implements PrestamosAdapter.OnPrestamoActionListener {

    private TextView tvKpiTotal, tvKpiCobrado, tvKpiPendiente;
    private RecyclerView rvPrestamos;
    private View btnNuevoPrestamo;
    private MaterialButton btnChipTodos, btnChipPendientes, btnChipPagados;

    private PrestamoRepository repository;
    private PrestamosAdapter adapter;
    private String currentFilter = "TODOS"; // TODOS, PENDIENTES, PAGADOS

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_prestamos, container, false);

        tvKpiTotal = view.findViewById(R.id.tv_prest_kpi_total);
        tvKpiCobrado = view.findViewById(R.id.tv_prest_kpi_cobrado);
        tvKpiPendiente = view.findViewById(R.id.tv_prest_kpi_pendiente);
        rvPrestamos = view.findViewById(R.id.rv_prestamos);
        btnNuevoPrestamo = view.findViewById(R.id.btn_nuevo_prestamo);

        btnChipTodos = view.findViewById(R.id.btn_chip_todos);
        btnChipPendientes = view.findViewById(R.id.btn_chip_pendientes);
        btnChipPagados = view.findViewById(R.id.btn_chip_pagados);

        repository = new PrestamoRepository(requireContext());
        rvPrestamos.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PrestamosAdapter(this);
        rvPrestamos.setAdapter(adapter);

        btnNuevoPrestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoFormularioPrestamo(null);
            }
        });

        if (btnChipTodos != null) {
            btnChipTodos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "TODOS";
                    actualizarEstiloChips();
                    loadData();
                }
            });
        }

        if (btnChipPendientes != null) {
            btnChipPendientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "PENDIENTES";
                    actualizarEstiloChips();
                    loadData();
                }
            });
        }

        if (btnChipPagados != null) {
            btnChipPagados.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFilter = "PAGADOS";
                    actualizarEstiloChips();
                    loadData();
                }
            });
        }

        actualizarEstiloChips();
        loadData();

        return view;
    }

    private void actualizarEstiloChips() {
        if (btnChipTodos == null) return;

        btnChipTodos.setBackgroundColor(Color.parseColor("#0F172A"));
        btnChipTodos.setTextColor(Color.parseColor("#94A3B8"));
        btnChipPendientes.setBackgroundColor(Color.parseColor("#0F172A"));
        btnChipPendientes.setTextColor(Color.parseColor("#F59E0B"));
        btnChipPagados.setBackgroundColor(Color.parseColor("#0F172A"));
        btnChipPagados.setTextColor(Color.parseColor("#10B981"));

        if (currentFilter.equals("TODOS")) {
            btnChipTodos.setBackgroundColor(Color.parseColor("#38BDF8"));
            btnChipTodos.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (currentFilter.equals("PENDIENTES")) {
            btnChipPendientes.setBackgroundColor(Color.parseColor("#F59E0B"));
            btnChipPendientes.setTextColor(Color.parseColor("#FFFFFF"));
        } else if (currentFilter.equals("PAGADOS")) {
            btnChipPagados.setBackgroundColor(Color.parseColor("#10B981"));
            btnChipPagados.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void loadData() {
        List<Prestamo> allList = repository.getAllPrestamos();
        List<Prestamo> filteredList = new ArrayList<>();

        double totalPrestado = 0;
        double totalCobrado = 0;
        double totalPendiente = 0;

        for (Prestamo p : allList) {
            totalPrestado += p.getMonto();
            totalCobrado += p.getMontoPagado();
            totalPendiente += p.getSaldoPendiente();

            String est = p.getEstado() != null ? p.getEstado().toUpperCase() : "PENDIENTE";

            if (currentFilter.equals("TODOS")) {
                filteredList.add(p);
            } else if (currentFilter.equals("PENDIENTES") && !est.equals("PAGADO")) {
                filteredList.add(p);
            } else if (currentFilter.equals("PAGADOS") && est.equals("PAGADO")) {
                filteredList.add(p);
            }
        }

        adapter.setList(filteredList);

        DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
        symbolsPE.setGroupingSeparator(',');
        symbolsPE.setDecimalSeparator('.');
        DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);

        tvKpiTotal.setText("S/. " + dfSoles.format(totalPrestado));
        tvKpiCobrado.setText("S/. " + dfSoles.format(totalCobrado));
        tvKpiPendiente.setText("S/. " + dfSoles.format(totalPendiente));
    }

    private void mostrarDialogoFormularioPrestamo(final Prestamo exist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(exist == null ? "Nuevo Préstamo / Adelanto" : "Editar Préstamo");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText etCliente = new EditText(requireContext());
        etCliente.setHint("Nombre del cliente");
        if (exist != null) etCliente.setText(exist.getCliente());
        layout.addView(etCliente);

        final EditText etMotivo = new EditText(requireContext());
        etMotivo.setHint("Motivo / Concepto (ej. Adelanto de compra)");
        if (exist != null) etMotivo.setText(exist.getMotivo());
        layout.addView(etMotivo);

        final EditText etMonto = new EditText(requireContext());
        etMonto.setHint("Monto total (S/.)");
        etMonto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (exist != null) etMonto.setText(String.valueOf(exist.getMonto()));
        layout.addView(etMonto);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String cli = etCliente.getText().toString().trim();
                String mot = etMotivo.getText().toString().trim();
                String monStr = etMonto.getText().toString().trim();

                if (cli.isEmpty()) {
                    Toast.makeText(requireContext(), "Ingrese el nombre del cliente", Toast.LENGTH_SHORT).show();
                    return;
                }

                double monto = 0;
                try {
                    monto = Double.parseDouble(monStr);
                } catch (Exception ignored) {}

                if (monto <= 0) {
                    Toast.makeText(requireContext(), "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                String fechaStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

                if (exist == null) {
                    Prestamo p = new Prestamo();
                    p.setCliente(cli);
                    p.setMotivo(mot);
                    p.setMonto(monto);
                    p.setMontoPagado(0);
                    p.setFecha(fechaStr);
                    p.setEstado("PENDIENTE");
                    repository.insert(p);
                    Toast.makeText(requireContext(), "Préstamo registrado", Toast.LENGTH_SHORT).show();
                } else {
                    exist.setCliente(cli);
                    exist.setMotivo(mot);
                    exist.setMonto(monto);
                    repository.update(exist);
                    Toast.makeText(requireContext(), "Préstamo actualizado", Toast.LENGTH_SHORT).show();
                }

                loadData();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    public void onAbonar(final Prestamo prestamo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Registrar Pago / Adelanto");
        builder.setMessage("Cliente: " + prestamo.getCliente() + "\nSaldo pendiente: S/. " + String.format(Locale.US, "%.2f", prestamo.getSaldoPendiente()));

        final EditText etAbono = new EditText(requireContext());
        etAbono.setHint("Monto abonado (S/.)");
        etAbono.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(etAbono);

        builder.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = etAbono.getText().toString().trim();
                double abono = 0;
                try {
                    abono = Double.parseDouble(str);
                } catch (Exception ignored) {}

                if (abono <= 0) {
                    Toast.makeText(requireContext(), "Monto de abono no válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean ok = repository.registrarAbono(prestamo.getId(), abono);
                if (ok) {
                    Toast.makeText(requireContext(), "Pago registrado con éxito", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @Override
    public void onMarcarPagado(final Prestamo prestamo) {
        prestamo.setMontoPagado(prestamo.getMonto());
        prestamo.setEstado("PAGADO");
        repository.update(prestamo);
        Toast.makeText(requireContext(), "Marcado como PAGADO total", Toast.LENGTH_SHORT).show();
        loadData();
    }

    @Override
    public void onEditar(Prestamo prestamo) {
        mostrarDialogoFormularioPrestamo(prestamo);
    }

    @Override
    public void onEliminar(Prestamo prestamo) {
        if (prestamo.getId() != null) {
            repository.delete(prestamo.getId());
            Toast.makeText(requireContext(), "Préstamo eliminado", Toast.LENGTH_SHORT).show();
            loadData();
        }
    }
}
