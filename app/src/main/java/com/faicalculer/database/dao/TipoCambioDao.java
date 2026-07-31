package com.faicalculer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.faicalculer.model.TipoCambio;

import java.util.List;

@Dao
public interface TipoCambioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TipoCambio tipoCambio);

    @Update
    int update(TipoCambio tipoCambio);

    @Query("SELECT * FROM tipo_cambio WHERE fecha >= :dateLimitString ORDER BY fecha DESC")
    List<TipoCambio> getHistoryFromDate(String dateLimitString);

    @Query("UPDATE tipo_cambio SET precio_compra = :precioCompra, precio_venta = :precioVenta, fuente = :fuente WHERE id = :id")
    int updateManual(long id, double precioCompra, double precioVenta, String fuente);

    @Query("DELETE FROM tipo_cambio WHERE id = :id")
    int deleteById(long id);
}
