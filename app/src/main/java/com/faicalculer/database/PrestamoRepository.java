package com.faicalculer.database;

import android.content.Context;
import com.faicalculer.database.dao.PrestamoDao;
import com.faicalculer.model.Prestamo;

import java.util.List;

/**
 * Repositorio refactorizado para manejar operaciones CRUD sobre la tabla 'prestamo' usando Jetpack Room.
 */
public class PrestamoRepository {

    private final PrestamoDao prestamoDao;

    public PrestamoRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.prestamoDao = db.prestamoDao();
    }

    public long insert(Prestamo prestamo) {
        long id = prestamoDao.insert(prestamo);
        if (id != -1) {
            prestamo.setId(id);
        }
        return id;
    }

    public int update(Prestamo prestamo) {
        if (prestamo == null || prestamo.getId() == null) return 0;
        return prestamoDao.update(prestamo);
    }

    public boolean registrarAbono(long id, double montoAbonado) {
        Prestamo p = getById(id);
        if (p == null) return false;

        double nuevoPagado = p.getMontoPagado() + montoAbonado;
        p.setMontoPagado(nuevoPagado);

        if (nuevoPagado >= p.getMonto()) {
            p.setEstado("PAGADO");
        } else if (nuevoPagado > 0) {
            p.setEstado("ADELANTO");
        }
        return update(p) > 0;
    }

    public int delete(long id) {
        return prestamoDao.deleteById(id);
    }

    public Prestamo getById(long id) {
        return prestamoDao.getById(id);
    }

    public List<Prestamo> getAllPrestamos() {
        return prestamoDao.getAllPrestamos();
    }
}
