package com.faicalculer.database;

import android.content.Context;
import com.faicalculer.database.dao.CalculoDao;
import com.faicalculer.model.Calculo;

import java.util.List;

/**
 * Repositorio refactorizado para manejar operaciones CRUD sobre la tabla 'calculo' usando Jetpack Room.
 */
public class CalculoRepository {

    private final CalculoDao calculoDao;

    public CalculoRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.calculoDao = db.calculoDao();
    }

    public long insert(Calculo c) {
        return calculoDao.insert(c);
    }

    public List<Calculo> getAllCalculos() {
        return calculoDao.getAllCalculos();
    }

    public boolean update(Calculo c) {
        if (c == null || c.getId() == null) return false;
        return calculoDao.update(c) > 0;
    }

    public boolean delete(long id) {
        return calculoDao.deleteById(id) > 0;
    }
}
