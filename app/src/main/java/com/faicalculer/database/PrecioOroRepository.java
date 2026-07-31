package com.faicalculer.database;

import android.content.Context;
import com.faicalculer.database.dao.PrecioOroDao;
import com.faicalculer.model.PrecioOro;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Repositorio refactorizado para manejar operaciones CRUD sobre la tabla 'precio_oro' usando Jetpack Room.
 */
public class PrecioOroRepository {

    private final PrecioOroDao precioOroDao;

    public PrecioOroRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.precioOroDao = db.precioOroDao();
    }

    public long insert(PrecioOro precioOro) {
        return precioOroDao.insert(precioOro);
    }

    public List<PrecioOro> getHistoryForLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateLimitString = sdf.format(cal.getTime());

        return precioOroDao.getHistoryFromDate(dateLimitString);
    }

    public boolean update(PrecioOro po) {
        if (po == null || po.getId() == null) return false;
        return precioOroDao.update(po) > 0;
    }

    public boolean delete(long id) {
        return precioOroDao.deleteById(id) > 0;
    }
}
