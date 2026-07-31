package com.faicalculer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.faicalculer.database.dao.CalculoDao;
import com.faicalculer.database.dao.PrecioOroDao;
import com.faicalculer.database.dao.PrestamoDao;
import com.faicalculer.database.dao.TipoCambioDao;
import com.faicalculer.model.Calculo;
import com.faicalculer.model.PrecioOro;
import com.faicalculer.model.Prestamo;
import com.faicalculer.model.TipoCambio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base de datos principal de Room para FaiCalculer.
 * Administra las entidades Calculo, Prestamo, PrecioOro y TipoCambio.
 */
@Database(
        entities = {Calculo.class, Prestamo.class, PrecioOro.class, TipoCambio.class},
        version = 7,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "faicalculer.db";
    private static volatile AppDatabase instance;

    // ExecutorService para ejecutar escrituras y lecturas pesadas fuera del Main Thread
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract CalculoDao calculoDao();
    public abstract PrestamoDao prestamoDao();
    public abstract PrecioOroDao precioOroDao();
    public abstract TipoCambioDao tipoCambioDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries() // Permitir consultas sincrónicas para compatibilidad directa manteniendo la API actual
            .build();
        }
        return instance;
    }
}
