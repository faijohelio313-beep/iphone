package com.faicalculer;

/**
 * Interfaz para propagar la acción del botón de retroceso hacia los fragments.
 */
public interface OnBackPressedListener {
    /**
     * Se ejecuta al presionar el botón de retroceso.
     * @return true si el fragment manejó el evento (por ejemplo, retrocedió en el WebView),
     *         false si el evento debe ser manejado por la actividad padre (por ejemplo, cerrar la app).
     */
    boolean onBackPressed();
}
