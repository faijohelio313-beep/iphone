package com.faicalculer;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.faicalculer.database.CalculoRepository;
import com.faicalculer.model.Calculo;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnTopCalculo, btnTopOnza, btnTopDolar;
    private Button btnSubRegistro, btnSubPromedio, btnSubAcumulado, btnSubPrestamos;
    private View layoutSubtabs;

    private String activeSubtab = "REGISTRO";
    private CalculoRepository calculoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculoRepository = new CalculoRepository(this);

        // Solicitar permisos runtime de Bluetooth en Android 12+ para impresora Advance ADV-8011N
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                new String[]{
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN
                },
                101
            );
        }

        btnTopCalculo = findViewById(R.id.btn_top_calculo);
        btnTopOnza = findViewById(R.id.btn_top_onza);
        btnTopDolar = findViewById(R.id.btn_top_dolar);

        layoutSubtabs = findViewById(R.id.layout_subtabs);
        btnSubRegistro = findViewById(R.id.btn_sub_registro);
        btnSubPromedio = findViewById(R.id.btn_sub_promedio);
        btnSubAcumulado = findViewById(R.id.btn_sub_acumulado);
        btnSubPrestamos = findViewById(R.id.btn_sub_prestamos);

        // Listeners para pestañas superiores
        btnTopCalculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTopTab("CALCULO");
            }
        });

        btnTopOnza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTopTab("ONZA");
            }
        });

        btnTopDolar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTopTab("DOLAR");
            }
        });

        // Listeners para sub-pestañas
        btnSubRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubTab("REGISTRO");
            }
        });

        btnSubPromedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubTab("PROMEDIO");
            }
        });

        btnSubAcumulado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubTab("ACUMULADO");
            }
        });

        btnSubPrestamos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSubTab("PRESTAMOS");
            }
        });

        if (savedInstanceState == null) {
            selectTopTab("CALCULO");
        }
    }

    private void selectTopTab(String tab) {
        btnTopCalculo.setTextColor(Color.parseColor("#64748B"));
        btnTopOnza.setTextColor(Color.parseColor("#64748B"));
        btnTopDolar.setTextColor(Color.parseColor("#64748B"));

        if (tab.equals("CALCULO")) {
            btnTopCalculo.setTextColor(Color.parseColor("#0F1E36"));
            layoutSubtabs.setVisibility(View.VISIBLE);
            selectSubTab(activeSubtab);
        } else if (tab.equals("ONZA")) {
            btnTopOnza.setTextColor(Color.parseColor("#0F1E36"));
            layoutSubtabs.setVisibility(View.GONE);
            replaceFragment(new OroFragment(), "ONZA");
        } else if (tab.equals("DOLAR")) {
            btnTopDolar.setTextColor(Color.parseColor("#0F1E36"));
            layoutSubtabs.setVisibility(View.GONE);
            replaceFragment(new DolarFragment(), "DOLAR");
        }
    }

    public void selectSubTab(String subtab) {
        activeSubtab = subtab;

        // Resetear estilos de botones de sub-pestañas
        resetSubtabButton(btnSubRegistro);
        resetSubtabButton(btnSubPromedio);
        resetSubtabButton(btnSubAcumulado);
        resetSubtabButton(btnSubPrestamos);

        Fragment fragment = null;

        if (subtab.equals("REGISTRO")) {
            setSubtabButtonActive(btnSubRegistro);
            fragment = new RegistroFragment();
        } else if (subtab.equals("PROMEDIO")) {
            setSubtabButtonActive(btnSubPromedio);
            fragment = new PromedioFragment();
        } else if (subtab.equals("ACUMULADO")) {
            setSubtabButtonActive(btnSubAcumulado);
            fragment = new RegistroAcumuladoFragment();
        } else if (subtab.equals("PRESTAMOS")) {
            setSubtabButtonActive(btnSubPrestamos);
            fragment = new PrestamosFragment();
        }

        if (fragment != null) {
            replaceFragment(fragment, subtab);
        }
    }

    public void abrirPromedioParaCliente(String nombreCliente) {
        activeSubtab = "PROMEDIO";
        resetSubtabButton(btnSubRegistro);
        resetSubtabButton(btnSubPromedio);
        resetSubtabButton(btnSubAcumulado);
        resetSubtabButton(btnSubPrestamos);

        setSubtabButtonActive(btnSubPromedio);

        PromedioFragment fragment = PromedioFragment.newInstance(nombreCliente);
        replaceFragment(fragment, "PROMEDIO");
    }

    private void resetSubtabButton(Button btn) {
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btn.setTextColor(Color.parseColor("#0F172A"));
    }

    private void setSubtabButtonActive(Button btn) {
        btn.setBackgroundColor(Color.parseColor("#0F1E36"));
        btn.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        ft.commit();
    }

    private static class MaterialFormHolder {
        View cardView;
        TextView tvTitulo;
        ImageView btnRemove;
        EditText etLey;
        EditText etPesoSinFundir;
        EditText etPesoFundido;
        TextView tvMerma;
        TextView tvPrecioTotal;
        double calculatedPrecioUsd = 0;
        double calculatedPrecioSoles = 0;
        double calculatedPrecioTotal = 0;
    }

    public void showCalculoDialog(Calculo calculoExistente) {
        showCalculoFormDialog(calculoExistente);
    }

    /**
     * Muestra el diálogo modal para crear o editar registros de Cálculo Multi-Material.
     */
    public void showCalculoFormDialog(final Calculo calculoExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_calculo_form, null);
        builder.setView(dialogView);

        final View btnClose = dialogView.findViewById(R.id.btn_dialog_close);
        final EditText etCliente = dialogView.findViewById(R.id.et_form_cliente);
        final EditText etOnza = dialogView.findViewById(R.id.et_form_onza);
        final EditText etLey = dialogView.findViewById(R.id.et_form_ley);
        final EditText etPorcentaje = dialogView.findViewById(R.id.et_form_porcentaje);
        final EditText etTc = dialogView.findViewById(R.id.et_form_tc);
        final EditText etPrecioUsd = dialogView.findViewById(R.id.et_form_precio_usd);
        final EditText etPrecioSoles = dialogView.findViewById(R.id.et_form_precio_soles);

        final LinearLayout containerMateriales = dialogView.findViewById(R.id.container_form_materiales);
        final MaterialButton btnAddMaterial = dialogView.findViewById(R.id.btn_form_add_material);

        final EditText etDescMotivo = dialogView.findViewById(R.id.et_form_desc_motivo);
        final EditText etDescMonto = dialogView.findViewById(R.id.et_form_desc_monto);
        final TextView tvPagoTotal = dialogView.findViewById(R.id.tv_form_pago_total);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btn_form_guardar_cambios);

        final AlertDialog dialog = builder.create();

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        final List<MaterialFormHolder> materialHolders = new ArrayList<>();

        Runnable updateFormTotalsRunnable = () -> calculateMultiMaterialTotals(etOnza, etLey, etPorcentaje, etTc, etPrecioUsd, etPrecioSoles, materialHolders, etDescMonto, tvPagoTotal);

        TextWatcher globalWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateFormTotalsRunnable.run();
            }
        };

        etOnza.addTextChangedListener(globalWatcher);
        etLey.addTextChangedListener(globalWatcher);
        etPorcentaje.addTextChangedListener(globalWatcher);
        etTc.addTextChangedListener(globalWatcher);
        etDescMonto.addTextChangedListener(globalWatcher);

        if (calculoExistente != null) {
            java.text.DecimalFormat dfClean = new java.text.DecimalFormat("0.###", new java.text.DecimalFormatSymbols(Locale.US));
            if (calculoExistente.getCliente() != null) etCliente.setText(calculoExistente.getCliente());
            if (calculoExistente.getOnza() > 0) etOnza.setText(dfClean.format(calculoExistente.getOnza()));
            if (calculoExistente.getLey() > 0) etLey.setText(dfClean.format(calculoExistente.getLey()));
            if (calculoExistente.getPorcentaje() > 0) etPorcentaje.setText(dfClean.format(calculoExistente.getPorcentaje()));
            if (calculoExistente.getTc() > 0) etTc.setText(dfClean.format(calculoExistente.getTc()));
            if (calculoExistente.getDescuentoMotivo() != null) etDescMotivo.setText(calculoExistente.getDescuentoMotivo());
            if (calculoExistente.getDescuentoMonto() > 0) etDescMonto.setText(String.format(Locale.US, "%.1f", calculoExistente.getDescuentoMonto()));

            addMaterialCardToContainer(containerMateriales, materialHolders, updateFormTotalsRunnable, etLey, calculoExistente);
        } else {
            addMaterialCardToContainer(containerMateriales, materialHolders, updateFormTotalsRunnable, etLey, null);
        }

        if (btnAddMaterial != null) {
            btnAddMaterial.setOnClickListener(v -> {
                addMaterialCardToContainer(containerMateriales, materialHolders, updateFormTotalsRunnable, etLey, null);
                updateFormTotalsRunnable.run();
            });
        }

        btnGuardar.setOnClickListener(v -> {
            String nombreCliente = etCliente != null ? etCliente.getText().toString().trim() : "";
            if (nombreCliente.isEmpty()) nombreCliente = "X";

            double onza = parseDouble(etOnza.getText().toString());
            double topLey = parseDouble(etLey.getText().toString());
            double pct = parseDouble(etPorcentaje.getText().toString());
            double tc = parseDouble(etTc.getText().toString());
            String descMot = etDescMotivo.getText().toString().trim();
            double descMonto = parseDouble(etDescMonto.getText().toString());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fecha = sdf.format(new Date());

            int countSaved = 0;
            for (int i = 0; i < materialHolders.size(); i++) {
                MaterialFormHolder h = materialHolders.get(i);
                double leyMat = parseDouble(h.etLey.getText().toString());
                if (leyMat <= 0) leyMat = topLey;

                double pesoSin = parseDouble(h.etPesoSinFundir.getText().toString());
                double pesoFun = parseDouble(h.etPesoFundido.getText().toString());
                double merma = (pesoSin > 0 && pesoFun > 0) ? Math.max(0, pesoSin - pesoFun) : 0;
                double pesoMat = pesoFun > 0 ? pesoFun : pesoSin;

                if (pesoMat <= 0 && leyMat <= 0 && materialHolders.size() > 1) {
                    continue;
                }

                double precioUsdPreciso = (onza / 31.1034768) * (leyMat / 100.0) * (1.0 - (pct / 100.0));
                double precioUsd = Math.floor(precioUsdPreciso * 100.0) / 100.0;
                double precioSolesPreciso = precioUsdPreciso * tc;
                double precioSoles = Math.floor(precioSolesPreciso * 100.0) / 100.0;
                double precioTotPreciso = pesoMat * precioSoles;
                double precioTot = Math.floor(precioTotPreciso * 100.0) / 100.0;
                double pagoMat = Math.max(0, precioTot - (i == 0 ? descMonto : 0));
                pagoMat = Math.floor(pagoMat * 100.0) / 100.0;

                Calculo c = (i == 0 && calculoExistente != null) ? calculoExistente : new Calculo();
                c.setCliente(nombreCliente);
                c.setFecha(fecha);
                c.setOnza(onza);
                c.setLey(leyMat);
                c.setPorcentaje(pct);
                c.setTc(tc);
                c.setPrecioUsd(precioUsd);
                c.setPrecioSoles(precioSoles);
                c.setPesoSinFundir(pesoSin);
                c.setPesoFundido(pesoFun);
                c.setMerma(merma);
                c.setPesoMaterial(pesoMat);
                c.setPrecioTotal(precioTot);
                c.setDescuentoMotivo(descMot);
                c.setDescuentoMonto(i == 0 ? descMonto : 0);
                c.setPagoTotal(pagoMat);

                if (c.getId() == null) {
                    calculoRepository.insert(c);
                } else {
                    calculoRepository.update(c);
                }
                countSaved++;
            }

            dialog.dismiss();

            Fragment current = getSupportFragmentManager().findFragmentByTag("REGISTRO");
            if (current instanceof RegistroFragment) {
                ((RegistroFragment) current).loadData();
            }

            Toast.makeText(MainActivity.this, "Lote de " + countSaved + " material(es) guardado correctamente", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
        updateFormTotalsRunnable.run();
    }

    private void addMaterialCardToContainer(LinearLayout containerMateriales, List<MaterialFormHolder> materialHolders, Runnable updateTotalsRunnable, EditText etTopLey, Calculo c) {
        View card = getLayoutInflater().inflate(R.layout.item_form_material_card, containerMateriales, false);
        MaterialFormHolder holder = new MaterialFormHolder();
        holder.cardView = card;
        holder.tvTitulo = card.findViewById(R.id.tv_item_material_titulo);
        holder.btnRemove = card.findViewById(R.id.btn_item_material_remove);
        holder.etLey = card.findViewById(R.id.et_item_material_ley);
        holder.etPesoSinFundir = card.findViewById(R.id.et_item_material_peso_sin_fundir);
        holder.etPesoFundido = card.findViewById(R.id.et_item_material_peso_fundido);
        holder.tvMerma = card.findViewById(R.id.tv_item_material_merma);
        holder.tvPrecioTotal = card.findViewById(R.id.tv_item_material_precio_total);

        // Pre-llenar con la Ley general si está ingresada
        if (etTopLey != null && !etTopLey.getText().toString().trim().isEmpty()) {
            holder.etLey.setText(etTopLey.getText().toString().trim());
        }

        materialHolders.add(holder);
        containerMateriales.addView(card);

        updateMaterialTitles(materialHolders);

        TextWatcher matWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateTotalsRunnable.run();
            }
        };

        holder.etLey.addTextChangedListener(matWatcher);
        holder.etPesoSinFundir.addTextChangedListener(matWatcher);
        holder.etPesoFundido.addTextChangedListener(matWatcher);

        holder.btnRemove.setOnClickListener(v -> {
            if (materialHolders.size() > 1) {
                materialHolders.remove(holder);
                containerMateriales.removeView(card);
                updateMaterialTitles(materialHolders);
                updateTotalsRunnable.run();
            } else {
                Toast.makeText(MainActivity.this, "Debe ingresar al menos 1 material", Toast.LENGTH_SHORT).show();
            }
        });

        if (c != null) {
            java.text.DecimalFormat dfClean = new java.text.DecimalFormat("0.###", new java.text.DecimalFormatSymbols(Locale.US));
            if (c.getLey() > 0) holder.etLey.setText(dfClean.format(c.getLey()));
            if (c.getPesoSinFundir() > 0) holder.etPesoSinFundir.setText(dfClean.format(c.getPesoSinFundir()));
            if (c.getPesoFundido() > 0) holder.etPesoFundido.setText(dfClean.format(c.getPesoFundido()));
        }
    }

    private void updateMaterialTitles(List<MaterialFormHolder> holders) {
        for (int i = 0; i < holders.size(); i++) {
            if (holders.get(i).tvTitulo != null) {
                holders.get(i).tvTitulo.setText("Material #" + (i + 1));
            }
        }
    }

    private void calculateMultiMaterialTotals(EditText etOnza, EditText etTopLey, EditText etPorcentaje, EditText etTc, EditText etPrecioUsd, EditText etPrecioSoles, List<MaterialFormHolder> holders, EditText etDescMonto, TextView tvPagoTotal) {
        try {
            double onza = parseDouble(etOnza.getText().toString());
            double topLey = parseDouble(etTopLey.getText().toString());
            double pct = parseDouble(etPorcentaje.getText().toString());
            double tc = parseDouble(etTc.getText().toString());
            double descMonto = parseDouble(etDescMonto.getText().toString());

            // Cotización base de la cabecera (usando Onza, Ley general, Desc %, TC)
            double precioUsdBasePreciso = (onza / 31.1034768) * (topLey / 100.0) * (1.0 - (pct / 100.0));
            double precioUsdBase = Math.floor(precioUsdBasePreciso * 100.0) / 100.0;
            if (etPrecioUsd != null && onza > 0 && topLey > 0) {
                etPrecioUsd.setText(String.format(Locale.US, "%.2f", precioUsdBase));
            }

            double precioSolesBase = 0;
            if (precioUsdBasePreciso > 0 && tc > 0) {
                double precioSolesBasePreciso = precioUsdBasePreciso * tc;
                precioSolesBase = Math.floor(precioSolesBasePreciso * 100.0) / 100.0;
                if (etPrecioSoles != null) {
                    etPrecioSoles.setText(String.format(Locale.US, "%.2f", precioSolesBase));
                }
            }

            java.text.DecimalFormatSymbols symbolsPE = new java.text.DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            java.text.DecimalFormat dfSoles = new java.text.DecimalFormat("#,##0.00", symbolsPE);
            java.text.DecimalFormat dfClean = new java.text.DecimalFormat("0.###", new java.text.DecimalFormatSymbols(Locale.US));

            double sumSubtotalMateriales = 0;

            for (int i = 0; i < holders.size(); i++) {
                MaterialFormHolder h = holders.get(i);
                double leyMat = parseDouble(h.etLey.getText().toString());
                if (leyMat <= 0) leyMat = topLey;

                double pesoSin = parseDouble(h.etPesoSinFundir.getText().toString());
                double pesoFun = parseDouble(h.etPesoFundido.getText().toString());

                if (h.tvMerma != null) {
                    if (pesoSin > 0 && pesoFun > 0) {
                        double merma = Math.max(0, pesoSin - pesoFun);
                        h.tvMerma.setText(dfClean.format(merma) + " g");
                    } else {
                        h.tvMerma.setText("0 g");
                    }
                }

                double pesoMat = pesoFun > 0 ? pesoFun : pesoSin;
                double precioUsdMatPreciso = (onza / 31.1034768) * (leyMat / 100.0) * (1.0 - (pct / 100.0));
                double precioSolesMatPreciso = precioUsdMatPreciso * tc;
                double precioSolesMat = Math.floor(precioSolesMatPreciso * 100.0) / 100.0;

                double precioTotMatPreciso = pesoMat * precioSolesMat;
                double precioTotMat = Math.floor(precioTotMatPreciso * 100.0) / 100.0;

                h.calculatedPrecioUsd = Math.floor(precioUsdMatPreciso * 100.0) / 100.0;
                h.calculatedPrecioSoles = precioSolesMat;
                h.calculatedPrecioTotal = precioTotMat;

                if (h.tvPrecioTotal != null) {
                    h.tvPrecioTotal.setText("S/. " + dfSoles.format(precioTotMat));
                }

                sumSubtotalMateriales += precioTotMat;
            }

            double pagoTotalFinal = Math.max(0, sumSubtotalMateriales - descMonto);
            pagoTotalFinal = Math.floor(pagoTotalFinal * 100.0) / 100.0;

            if (tvPagoTotal != null) {
                tvPagoTotal.setText("S/. " + dfSoles.format(pagoTotalFinal));
            }
        } catch (Exception e) {
            // Ignorar errores de edición parcial
        }
    }

    private double parseDouble(String val) {
        if (val == null || val.trim().isEmpty()) return 0;
        try {
            String clean = val.replace("S/.", "").replace("S/", "").replace(",", "").trim();
            return Double.parseDouble(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
