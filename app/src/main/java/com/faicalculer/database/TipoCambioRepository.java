package com.faicalculer.database;

import android.content.Context;
import com.faicalculer.database.dao.TipoCambioDao;
import com.faicalculer.model.TipoCambio;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Repositorio refactorizado para manejar operaciones CRUD sobre la tabla 'tipo_cambio' usando Jetpack Room.
 */
public class TipoCambioRepository {

    private final TipoCambioDao tipoCambioDao;

    public TipoCambioRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.tipoCambioDao = db.tipoCambioDao();
    }

    public long insert(TipoCambio tipoCambio) {
        return tipoCambioDao.insert(tipoCambio);
    }

    public List<TipoCambio> getHistoryForLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateLimitString = sdf.format(cal.getTime());

        return tipoCambioDao.getHistoryFromDate(dateLimitString);
    }

    public boolean update(TipoCambio tc) {
        if (tc == null || tc.getId() == null) return false;
        return tipoCambioDao.update(tc) > 0;
    }

    public boolean updateManual(long id, double precioCompra, double precioVenta) {
        return tipoCambioDao.updateManual(id, precioCompra, precioVenta, "Manual") > 0;
    }

    public boolean delete(long id) {
        return tipoCambioDao.deleteById(id) > 0;
    }
}
