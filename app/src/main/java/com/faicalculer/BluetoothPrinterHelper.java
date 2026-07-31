package com.faicalculer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.print.PrintHelper;

import com.faicalculer.model.Calculo;
import com.faicalculer.model.ClienteAcumulado;

import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class BluetoothPrinterHelper {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String PREF_KEY_PRINTER_MAC = "pref_last_printer_mac";

    public static void printCalculoTicket(final Context context, final View dummyView, final Calculo calculo) {
        if (context == null || calculo == null) return;

        try {
            View printView = LayoutInflater.from(context).inflate(R.layout.print_ticket_registro, null, false);

            DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
            DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

            TextView tvCliente = printView.findViewById(R.id.tv_print_cliente);
            TextView tvFecha = printView.findViewById(R.id.tv_print_fecha);
            TextView tvOnza = printView.findViewById(R.id.tv_print_onza);
            TextView tvLey = printView.findViewById(R.id.tv_print_ley);
            TextView tvPorcentaje = printView.findViewById(R.id.tv_print_porcentaje);
            TextView tvTc = printView.findViewById(R.id.tv_print_tc);
            TextView tvPrecioSoles = printView.findViewById(R.id.tv_print_precio_soles);
            TextView tvSinFundir = printView.findViewById(R.id.tv_print_peso_sin_fundir);
            TextView tvFundido = printView.findViewById(R.id.tv_print_peso_fundido);
            TextView tvMerma = printView.findViewById(R.id.tv_print_merma);
            TextView tvPrecioSolesCalc = printView.findViewById(R.id.tv_print_precio_soles_calc);
            TextView tvDescuento = printView.findViewById(R.id.tv_print_descuento);
            TextView tvPrecioTotal = printView.findViewById(R.id.tv_print_precio_total);
            TextView tvCantidadFundido = printView.findViewById(R.id.tv_print_cantidad_fundido);
            TextView tvPagoTotal = printView.findViewById(R.id.tv_print_pago_total);

            if (tvCliente != null) tvCliente.setText("Cliente: " + calculo.getCliente());
            if (tvFecha != null) tvFecha.setText("Fecha: " + (calculo.getFecha() != null ? calculo.getFecha() : ""));
            if (tvOnza != null) tvOnza.setText(dfClean.format(calculo.getOnza()));
            if (tvLey != null) tvLey.setText(dfClean.format(calculo.getLey()) + "%");
            if (tvPorcentaje != null) tvPorcentaje.setText(dfClean.format(calculo.getPorcentaje()) + "%");
            if (tvTc != null) tvTc.setText(dfClean.format(calculo.getTc()));
            if (tvPrecioSoles != null) tvPrecioSoles.setText("S/. " + dfSoles.format(calculo.getPrecioSoles()));
            if (tvPrecioSolesCalc != null) tvPrecioSolesCalc.setText("S/. " + dfSoles.format(calculo.getPrecioSoles()));
            if (tvDescuento != null) tvDescuento.setText("S/. " + dfSoles.format(calculo.getDescuentoMonto()));
            if (tvSinFundir != null) tvSinFundir.setText(dfClean.format(calculo.getPesoSinFundir()) + " g");
            if (tvFundido != null) tvFundido.setText(dfClean.format(calculo.getPesoFundido()) + " g");
            if (tvMerma != null) tvMerma.setText(dfClean.format(calculo.getMerma()) + " g");
            if (tvPrecioTotal != null) tvPrecioTotal.setText("S/. " + dfSoles.format(calculo.getPrecioTotal()));
            if (tvCantidadFundido != null) tvCantidadFundido.setText(dfClean.format(calculo.getPesoFundido()) + " g");
            if (tvPagoTotal != null) tvPagoTotal.setText("S/. " + dfSoles.format(calculo.getPagoTotal()));

            renderViewAndPrint(context, printView, "Ticket_" + calculo.getCliente());

        } catch (Exception e) {
            Toast.makeText(context, "Error preparando ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Muestra el ticket renderizado en pantalla en un diálogo para verificar el diseño
     * sin gastar papel de impresión.
     */
    public static void previewCalculoTicket(final Context context, final Calculo calculo) {
        if (context == null || calculo == null) return;

        try {
            View printView = LayoutInflater.from(context).inflate(R.layout.print_ticket_registro, null, false);

            DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
            DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

            TextView tvCliente = printView.findViewById(R.id.tv_print_cliente);
            TextView tvFecha = printView.findViewById(R.id.tv_print_fecha);
            TextView tvOnza = printView.findViewById(R.id.tv_print_onza);
            TextView tvLey = printView.findViewById(R.id.tv_print_ley);
            TextView tvPorcentaje = printView.findViewById(R.id.tv_print_porcentaje);
            TextView tvTc = printView.findViewById(R.id.tv_print_tc);
            TextView tvPrecioSoles = printView.findViewById(R.id.tv_print_precio_soles);
            TextView tvSinFundir = printView.findViewById(R.id.tv_print_peso_sin_fundir);
            TextView tvFundido = printView.findViewById(R.id.tv_print_peso_fundido);
            TextView tvMerma = printView.findViewById(R.id.tv_print_merma);
            TextView tvPrecioSolesCalc = printView.findViewById(R.id.tv_print_precio_soles_calc);
            TextView tvDescuento = printView.findViewById(R.id.tv_print_descuento);
            TextView tvPrecioTotal = printView.findViewById(R.id.tv_print_precio_total);
            TextView tvCantidadFundido = printView.findViewById(R.id.tv_print_cantidad_fundido);
            TextView tvPagoTotal = printView.findViewById(R.id.tv_print_pago_total);

            if (tvCliente != null) tvCliente.setText("Cliente: " + calculo.getCliente());
            if (tvFecha != null) tvFecha.setText("Fecha: " + (calculo.getFecha() != null ? calculo.getFecha() : ""));
            if (tvOnza != null) tvOnza.setText(dfClean.format(calculo.getOnza()));
            if (tvLey != null) tvLey.setText(dfClean.format(calculo.getLey()) + "%");
            if (tvPorcentaje != null) tvPorcentaje.setText(dfClean.format(calculo.getPorcentaje()) + "%");
            if (tvTc != null) tvTc.setText(dfClean.format(calculo.getTc()));
            if (tvPrecioSoles != null) tvPrecioSoles.setText("S/. " + dfSoles.format(calculo.getPrecioSoles()));
            if (tvPrecioSolesCalc != null) tvPrecioSolesCalc.setText("S/. " + dfSoles.format(calculo.getPrecioSoles()));
            if (tvDescuento != null) tvDescuento.setText("S/. " + dfSoles.format(calculo.getDescuentoMonto()));
            if (tvSinFundir != null) tvSinFundir.setText(dfClean.format(calculo.getPesoSinFundir()) + " g");
            if (tvFundido != null) tvFundido.setText(dfClean.format(calculo.getPesoFundido()) + " g");
            if (tvMerma != null) tvMerma.setText(dfClean.format(calculo.getMerma()) + " g");
            if (tvPrecioTotal != null) tvPrecioTotal.setText("S/. " + dfSoles.format(calculo.getPrecioTotal()));
            if (tvCantidadFundido != null) tvCantidadFundido.setText(dfClean.format(calculo.getPesoFundido()) + " g");
            if (tvPagoTotal != null) tvPagoTotal.setText("S/. " + dfSoles.format(calculo.getPagoTotal()));

            // Renderizar el ticket a 384px exactos (ancho del rollo térmico de 58mm) sin multiplicador de densidad
            int widthSpec = View.MeasureSpec.makeMeasureSpec(PRINTER_WIDTH_PX, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            printView.measure(widthSpec, heightSpec);
            int w = printView.getMeasuredWidth();
            int h = printView.getMeasuredHeight();
            printView.layout(0, 0, w, h);

            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bmp);
            cv.drawColor(Color.WHITE);
            printView.draw(cv);

            Bitmap preview = createThermalCleanBitmap(bmp);
            bmp.recycle();

            // Mostrar en un Dialog con ImageView escalado al ancho de la pantalla
            android.widget.ImageView iv = new android.widget.ImageView(context);
            iv.setImageBitmap(preview);
            iv.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
            iv.setBackgroundColor(Color.LTGRAY);
            iv.setPadding(16, 16, 16, 16);

            android.widget.ScrollView sv = new android.widget.ScrollView(context);
            sv.addView(iv);

            new AlertDialog.Builder(context)
                    .setTitle("👁️ Vista Previa del Ticket")
                    .setView(sv)
                    .setPositiveButton("🖨️ Imprimir ahora", (d, w2) -> {
                        final View dummy = new View(context);
                        BluetoothPrinterHelper.printCalculoTicket(context, dummy, calculo);
                    })
                    .setNegativeButton("Cerrar", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(context, "Error en vista previa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void previewAcumuladoTicket(final Context context, final ClienteAcumulado ca) {
        if (context == null || ca == null) return;

        try {
            View printView = LayoutInflater.from(context).inflate(R.layout.print_ticket_acumulado, null, false);

            DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
            DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

            TextView tvCliente = printView.findViewById(R.id.tv_print_acum_cliente);
            TextView tvFecha = printView.findViewById(R.id.tv_print_acum_fecha);
            LinearLayout containerMat = printView.findViewById(R.id.ll_print_acum_materiales);
            TextView tvTotalSinFun = printView.findViewById(R.id.tv_print_acum_total_sin_fun);
            TextView tvTotalFun = printView.findViewById(R.id.tv_print_acum_total_fun);
            TextView tvTotalMerma = printView.findViewById(R.id.tv_print_acum_total_merma);
            TextView tvSubtotal = printView.findViewById(R.id.tv_print_acum_subtotal);
            TextView tvDescuentoUniv = printView.findViewById(R.id.tv_print_acum_descuento_univ);
            TextView tvPagoFinal = printView.findViewById(R.id.tv_print_acum_pago_final);

            if (tvCliente != null) tvCliente.setText("ACUMULADO: “" + ca.getCliente() + "”");
            if (tvFecha != null) tvFecha.setText(ca.getFecha() != null ? ca.getFecha() : "");

            if (containerMat != null) {
                populateMaterialesContainer(context, containerMat, ca.getCalculos(), dfClean, dfSoles);
            }

            if (tvTotalSinFun != null) tvTotalSinFun.setText("Sin Fun: " + dfClean.format(ca.getTotalPesoSinFundir()) + "g");
            if (tvTotalFun != null) tvTotalFun.setText("Fundido: " + dfClean.format(ca.getTotalPesoFundido()) + "g");
            if (tvTotalMerma != null) tvTotalMerma.setText("Merma: " + dfClean.format(ca.getTotalMerma()) + "g");
            if (tvSubtotal != null) tvSubtotal.setText("S/. " + dfSoles.format(ca.getSubtotalPagoTotal()));
            if (tvDescuentoUniv != null) tvDescuentoUniv.setText("- S/. " + dfSoles.format(ca.getDescuentoUniversal()));
            if (tvPagoFinal != null) tvPagoFinal.setText("S/. " + dfSoles.format(ca.getPagoTotalFinal()));

            // Renderizar ticket acumulado como bitmap a PRINTER_WIDTH_PX (576px)
            int widthSpec = View.MeasureSpec.makeMeasureSpec(PRINTER_WIDTH_PX, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            printView.measure(widthSpec, heightSpec);
            int w = printView.getMeasuredWidth();
            int h = printView.getMeasuredHeight();
            printView.layout(0, 0, w, h);

            Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas cv = new Canvas(bmp);
            cv.drawColor(Color.WHITE);
            printView.draw(cv);

            Bitmap preview = createThermalCleanBitmap(bmp);
            bmp.recycle();

            // Mostrar en un Dialog con ImageView escalado al ancho de la pantalla
            android.widget.ImageView iv = new android.widget.ImageView(context);
            iv.setImageBitmap(preview);
            iv.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
            iv.setBackgroundColor(Color.LTGRAY);
            iv.setPadding(16, 16, 16, 16);

            android.widget.ScrollView sv = new android.widget.ScrollView(context);
            sv.addView(iv);

            new AlertDialog.Builder(context)
                    .setTitle("👁️ Vista Previa Ticket Acumulado")
                    .setView(sv)
                    .setPositiveButton("🖨️ Imprimir ahora", (d, w2) -> {
                        final View dummy = new View(context);
                        BluetoothPrinterHelper.printAcumuladoCard(context, dummy, ca);
                    })
                    .setNegativeButton("Cerrar", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(context, "Error en vista previa acumulado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public static void printAcumuladoCard(final Context context, final View dummyView, final ClienteAcumulado ca) {
        if (context == null || ca == null) return;

        try {
            View printView = LayoutInflater.from(context).inflate(R.layout.print_ticket_acumulado, null, false);

            DecimalFormatSymbols symbolsPE = new DecimalFormatSymbols(new Locale("es", "PE"));
            symbolsPE.setGroupingSeparator(',');
            symbolsPE.setDecimalSeparator('.');
            DecimalFormat dfSoles = new DecimalFormat("#,##0.00", symbolsPE);
            DecimalFormat dfClean = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));

            TextView tvCliente = printView.findViewById(R.id.tv_print_acum_cliente);
            TextView tvFecha = printView.findViewById(R.id.tv_print_acum_fecha);
            LinearLayout containerMat = printView.findViewById(R.id.ll_print_acum_materiales);
            TextView tvTotalSinFun = printView.findViewById(R.id.tv_print_acum_total_sin_fun);
            TextView tvTotalFun = printView.findViewById(R.id.tv_print_acum_total_fun);
            TextView tvTotalMerma = printView.findViewById(R.id.tv_print_acum_total_merma);
            TextView tvSubtotal = printView.findViewById(R.id.tv_print_acum_subtotal);
            TextView tvDescuentoUniv = printView.findViewById(R.id.tv_print_acum_descuento_univ);
            TextView tvPagoFinal = printView.findViewById(R.id.tv_print_acum_pago_final);

            if (tvCliente != null) tvCliente.setText("ACUMULADO: “" + ca.getCliente() + "”");
            if (tvFecha != null) tvFecha.setText(ca.getFecha() != null ? ca.getFecha() : "");

            if (containerMat != null) {
                populateMaterialesContainer(context, containerMat, ca.getCalculos(), dfClean, dfSoles);
            }

            if (tvTotalSinFun != null) tvTotalSinFun.setText("Sin Fun: " + dfClean.format(ca.getTotalPesoSinFundir()) + "g");
            if (tvTotalFun != null) tvTotalFun.setText("Fundido: " + dfClean.format(ca.getTotalPesoFundido()) + "g");
            if (tvTotalMerma != null) tvTotalMerma.setText("Merma: " + dfClean.format(ca.getTotalMerma()) + "g");
            if (tvSubtotal != null) tvSubtotal.setText("S/. " + dfSoles.format(ca.getSubtotalPagoTotal()));
            if (tvDescuentoUniv != null) tvDescuentoUniv.setText("- S/. " + dfSoles.format(ca.getDescuentoUniversal()));
            if (tvPagoFinal != null) tvPagoFinal.setText("S/. " + dfSoles.format(ca.getPagoTotalFinal()));

            renderViewAndPrint(context, printView, "Acumulado_" + ca.getCliente());

        } catch (Exception e) {
            Toast.makeText(context, "Error preparando acumulado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static void populateMaterialesContainer(Context context, LinearLayout containerMat, List<Calculo> calculos, DecimalFormat dfClean, DecimalFormat dfSoles) {
        if (containerMat == null || calculos == null) return;
        containerMat.removeAllViews();
        for (int i = 0; i < calculos.size(); i++) {
            Calculo c = calculos.get(i);
            LinearLayout matRow = new LinearLayout(context);
            matRow.setOrientation(LinearLayout.VERTICAL);
            matRow.setPadding(0, 4, 0, 4);

            // Fila 1: Material #X
            TextView tvFila1 = new TextView(context);
            tvFila1.setText("Material #" + (i + 1));
            tvFila1.setTextColor(Color.BLACK);
            tvFila1.setTextSize(8.0f);
            tvFila1.setTypeface(null, android.graphics.Typeface.BOLD);
            tvFila1.setPadding(0, 0, 0, 2);
            matRow.addView(tvFila1);

            // Fila 2: Oz   Ley   Desc %   Tc (Con separación holgada y distancia inferior)
            TextView tvFila2 = new TextView(context);
            tvFila2.setText("Oz: " + dfClean.format(c.getOnza()) + "    Ley: " + dfClean.format(c.getLey()) + "%    Desc: " + dfClean.format(c.getPorcentaje()) + "%    Tc: " + dfClean.format(c.getTc()));
            tvFila2.setTextColor(Color.BLACK);
            tvFila2.setTextSize(7.5f);
            tvFila2.setPadding(0, 1, 0, 3);
            matRow.addView(tvFila2);

            // Fila 3: Sin Fun   Fundido   Merma (Con separación holgada y distancia inferior)
            TextView tvFila3 = new TextView(context);
            tvFila3.setText("Sin Fun: " + dfClean.format(c.getPesoSinFundir()) + "g    Fundido: " + dfClean.format(c.getPesoFundido()) + "g    Merma: " + dfClean.format(c.getMerma()) + "g");
            tvFila3.setTextColor(Color.BLACK);
            tvFila3.setTextSize(7.5f);
            tvFila3.setPadding(0, 1, 0, 3);
            matRow.addView(tvFila3);

            // Fila 4: Gramos Fundido (IZQUIERDA) & Precio Soles (DERECHA) -> POSICIONES CAMBIADAS
            LinearLayout rowFila4 = new LinearLayout(context);
            rowFila4.setOrientation(LinearLayout.HORIZONTAL);
            rowFila4.setPadding(0, 1, 0, 3);

            TextView tvGramosFundido = new TextView(context);
            tvGramosFundido.setText("Gramos Fundido: " + dfClean.format(c.getPesoFundido()) + "g");
            tvGramosFundido.setTextColor(Color.BLACK);
            tvGramosFundido.setTextSize(7.5f);
            tvGramosFundido.setTypeface(null, android.graphics.Typeface.BOLD);
            tvGramosFundido.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

            TextView tvPrecioSoles = new TextView(context);
            tvPrecioSoles.setText("Precio Soles: S/. " + dfSoles.format(c.getPrecioSoles()));
            tvPrecioSoles.setTextColor(Color.BLACK);
            tvPrecioSoles.setTextSize(7.5f);
            tvPrecioSoles.setTypeface(null, android.graphics.Typeface.BOLD);
            tvPrecioSoles.setGravity(Gravity.END);

            rowFila4.addView(tvGramosFundido);
            rowFila4.addView(tvPrecioSoles);
            matRow.addView(rowFila4);

            // Fila 5: Pago Material (CENTRADO Y DESTACADO)
            TextView tvFila5 = new TextView(context);
            tvFila5.setText("Pago Material: S/. " + dfSoles.format(c.getPagoTotal()));
            tvFila5.setTextColor(Color.BLACK);
            tvFila5.setTextSize(8.0f);
            tvFila5.setTypeface(null, android.graphics.Typeface.BOLD);
            tvFila5.setGravity(Gravity.CENTER);
            tvFila5.setPadding(0, 2, 0, 4);
            matRow.addView(tvFila5);

            View line = new View(context);
            line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            line.setBackgroundColor(Color.GRAY);
            matRow.addView(line);

            containerMat.addView(matRow);
        }
    }

    public static void printAcumuladoCard(final Context context, final View dummyView, final String nombreCliente) {
        if (context == null) return;
        Toast.makeText(context, "Imprimiendo acumulado de " + nombreCliente, Toast.LENGTH_SHORT).show();
    }

    // Ancho de impresión para papel térmico (576 puntos / 72 bytes por línea para 80mm/72mm de área imprimible)
    private static final int PRINTER_WIDTH_PX = 576;

    private static void renderViewAndPrint(final Context context, final View printView, final String tag) {
        // Medir la vista a 576px exactos (MeasureSpec.EXACTLY) sin multiplicador de densidad del celular
        int widthSpec = View.MeasureSpec.makeMeasureSpec(PRINTER_WIDTH_PX, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        printView.measure(widthSpec, heightSpec);
        int width = printView.getMeasuredWidth(); // Exactamente 384px
        int height = printView.getMeasuredHeight();

        printView.layout(0, 0, width, height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        printView.draw(canvas);

        final Bitmap thermalBitmap = createThermalCleanBitmap(bitmap);
        bitmap.recycle();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter == null) {
            Toast.makeText(context, "Tu celular no posee Bluetooth. Usando impresor de sistema...", Toast.LENGTH_SHORT).show();
            imprimirConSistemaPrint(context, thermalBitmap, tag);
            return;
        }

        if (!adapter.isEnabled()) {
            mostrarDialogoActivarBluetooth(context, thermalBitmap, tag);
            return;
        }

        Set<BluetoothDevice> pairedDevices = null;
        try {
            pairedDevices = adapter.getBondedDevices();
        } catch (SecurityException ignored) {}

        if (pairedDevices == null || pairedDevices.isEmpty()) {
            mostrarDialogoVincularImpresora(context, thermalBitmap, tag);
            return;
        }

        final List<BluetoothDevice> deviceList = new ArrayList<>(pairedDevices);
        SharedPreferences prefs = context.getSharedPreferences("FaiCalculerPrefs", Context.MODE_PRIVATE);
        String lastMac = prefs.getString(PREF_KEY_PRINTER_MAC, null);

        BluetoothDevice savedDevice = null;
        BluetoothDevice keywordDevice = null;

        for (BluetoothDevice d : deviceList) {
            try {
                if (lastMac != null && d.getAddress().equalsIgnoreCase(lastMac)) {
                    savedDevice = d;
                    break;
                }
                String name = d.getName();
                if (name != null) {
                    String nameUpper = name.toUpperCase();
                    if (nameUpper.contains("ADV") || nameUpper.contains("8011") || nameUpper.contains("PRINTER") || nameUpper.contains("POS") || nameUpper.contains("BT") || nameUpper.contains("MTP") || nameUpper.contains("RPP")) {
                        keywordDevice = d;
                    }
                }
            } catch (SecurityException ignored) {}
        }

        BluetoothDevice targetDevice = savedDevice != null ? savedDevice : (keywordDevice != null ? keywordDevice : (deviceList.size() == 1 ? deviceList.get(0) : null));

        if (targetDevice != null) {
            enviarImpresionDirecta(context, targetDevice, thermalBitmap, tag);
        } else {
            mostrarSelectorDispositivoBluetooth(context, deviceList, thermalBitmap, tag);
        }
    }

    private static void mostrarDialogoActivarBluetooth(final Context context, final Bitmap thermalBitmap, final String tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("⚠️ Bluetooth Desactivado");
        builder.setMessage("Para imprimir directamente en tu impresora Advance ADV-8011N, activa el Bluetooth de tu celular.");
        builder.setPositiveButton("📶 Abrir Ajustes de Bluetooth", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Abre Ajustes > Bluetooth en tu celular", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Imprimir vía Sistema/PDF", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imprimirConSistemaPrint(context, thermalBitmap, tag);
            }
        });
        builder.show();
    }

    private static void mostrarDialogoVincularImpresora(final Context context, final Bitmap thermalBitmap, final String tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("🔍 Vincular Impresora Bluetooth");
        builder.setMessage("Tu celular no tiene ninguna impresora Bluetooth vinculada todavía.\n\nPor favor vincula tu impresora Advance ADV-8011N en los Ajustes de Bluetooth de tu celular para imprimir con 1 solo toque.");
        builder.setPositiveButton("📶 Abrir Ajustes de Bluetooth para Vincular", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Abre Ajustes > Bluetooth en tu celular", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Imprimir vía Sistema/PDF", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imprimirConSistemaPrint(context, thermalBitmap, tag);
            }
        });
        builder.show();
    }

    public static void mostrarSelectorDispositivoBluetooth(final Context context, final List<BluetoothDevice> deviceList, final Bitmap thermalBitmap, final String tag) {
        List<String> displayNames = new ArrayList<>();
        for (BluetoothDevice d : deviceList) {
            try {
                String n = d.getName();
                displayNames.add("🖨️ " + (n != null ? n : "Dispositivo Bluetooth") + " (" + d.getAddress() + ")");
            } catch (SecurityException e) {
                displayNames.add("🖨️ " + d.getAddress());
            }
        }
        displayNames.add("📄 Usar Servicio de Impresión Android (PDF)");

        CharSequence[] items = displayNames.toArray(new CharSequence[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Selecciona tu Impresora Bluetooth");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which < deviceList.size()) {
                    BluetoothDevice selected = deviceList.get(which);
                    try {
                        context.getSharedPreferences("FaiCalculerPrefs", Context.MODE_PRIVATE)
                                .edit().putString(PREF_KEY_PRINTER_MAC, selected.getAddress()).apply();
                    } catch (Exception ignored) {}
                    enviarImpresionDirecta(context, selected, thermalBitmap, tag);
                } else {
                    imprimirConSistemaPrint(context, thermalBitmap, tag);
                }
            }
        });
        builder.show();
    }

    private static void enviarImpresionDirecta(final Context context, final BluetoothDevice device, final Bitmap thermalBitmap, final String tag) {
        String deviceName = "Impresora";
        try {
            if (device.getName() != null) deviceName = device.getName();
        } catch (SecurityException ignored) {}

        final String nameShow = deviceName;
        Toast.makeText(context, "Enviando ticket a " + nameShow + "...", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean success = sendBitmapDirectEscPos(device, thermalBitmap);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(context, "¡Impresión exitosa en " + nameShow + "!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Bluetooth no respondió en " + nameShow + ". Abriendo selector...", Toast.LENGTH_SHORT).show();
                            imprimirConSistemaPrint(context, thermalBitmap, tag);
                        }
                    }
                });
            }
        }).start();
    }

    private static void imprimirConSistemaPrint(Context context, Bitmap bitmap, String tag) {
        try {
            PrintHelper printHelper = new PrintHelper(context);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FILL);
            printHelper.printBitmap("Fajio_" + tag, bitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error al abrir servicio de impresión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean sendBitmapDirectEscPos(BluetoothDevice device, Bitmap bitmap) {
        BluetoothSocket socket = null;
        try {
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID);
            socket.connect();

            OutputStream out = socket.getOutputStream();

            byte[] INIT = new byte[]{0x1B, 0x40}; // Limpiar memoria y resetear impresora a modo comando
            byte[] ALIGN_LEFT = new byte[]{0x1B, 0x61, 0x00};
            byte[] LINE_SPACE_0 = new byte[]{0x1B, 0x33, 0x00};
            byte[] FEED_3 = new byte[]{0x1B, 0x64, 0x03};

            out.write(INIT);
            out.write(ALIGN_LEFT);
            out.write(LINE_SPACE_0);
            out.flush();
            try { Thread.sleep(30); } catch (Exception ignored) {}

            int width = bitmap.getWidth(); // 384 puntos (48mm a 203 DPI)
            int height = bitmap.getHeight();
            int widthBytes = (width + 7) / 8; // 48 bytes por fila

            // Enviar en bloques seguros de 48 filas para evitar desbordamiento del buffer en la tiqueteras 58mm
            int sliceHeight = 48;
            for (int top = 0; top < height; top += sliceHeight) {
                int currentSlice = Math.min(sliceHeight, height - top);

                byte[] header = new byte[]{
                        0x1D, 0x76, 0x30, 0x00,
                        (byte) (widthBytes % 256),
                        (byte) (widthBytes / 256),
                        (byte) (currentSlice % 256),
                        (byte) (currentSlice / 256)
                };
                out.write(header);

                for (int y = top; y < top + currentSlice; y++) {
                    for (int x = 0; x < widthBytes; x++) {
                        byte b = 0;
                        for (int bit = 0; bit < 8; bit++) {
                            int pixelX = x * 8 + bit;
                            if (pixelX < width) {
                                int color = bitmap.getPixel(pixelX, y);
                                if (color == Color.BLACK) {
                                    b |= (1 << (7 - bit));
                                }
                            }
                        }
                        out.write(b);
                    }
                }
                out.flush();
                try { Thread.sleep(15); } catch (Exception ignored) {}
            }

            out.write(FEED_3);
            out.flush();
            out.close();
            socket.close();
            return true;
        } catch (Exception e) {
            if (socket != null) {
                try { socket.close(); } catch (Exception ignored) {}
            }
            return false;
        }
    }

    private static Bitmap createThermalCleanBitmap(Bitmap src) {
        int width = src.getWidth();
        int height = src.getHeight();

        // Encontrar la primera fila con contenido impreso (eliminar espacio en blanco superior)
        int topY = 0;
        for (int y = 0; y < height; y++) {
            boolean rowHasContent = false;
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                double luminance = (0.299 * r) + (0.587 * g) + (0.114 * b);
                if (luminance < 240) {
                    rowHasContent = true;
                    break;
                }
            }
            if (rowHasContent) {
                topY = y;
                break;
            }
        }

        // Encontrar la última fila con contenido impreso
        int bottomY = height - 1;
        for (int y = height - 1; y >= topY; y--) {
            boolean rowHasContent = false;
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                double luminance = (0.299 * r) + (0.587 * g) + (0.114 * b);
                if (luminance < 240) {
                    rowHasContent = true;
                    break;
                }
            }
            if (rowHasContent) {
                bottomY = y;
                break;
            }
        }

        int croppedHeight = Math.max(1, bottomY - topY + 1);
        Bitmap thermalBitmap = Bitmap.createBitmap(width, croppedHeight, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < croppedHeight; y++) {
            int srcY = topY + y;
            for (int x = 0; x < width; x++) {
                int pixel = src.getPixel(x, srcY);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                double luminance = (0.299 * r) + (0.587 * g) + (0.114 * b);

                if (luminance < 180) {
                    thermalBitmap.setPixel(x, y, Color.BLACK);
                } else {
                    thermalBitmap.setPixel(x, y, Color.WHITE);
                }
            }
        }
        return thermalBitmap;
    }
}
