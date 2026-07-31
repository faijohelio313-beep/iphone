package com.faicalculer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.faicalculer.model.PrecioOro;

import java.util.List;

@Dao
public interface PrecioOroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PrecioOro precioOro);

    @Update
    int update(PrecioOro precioOro);

    @Query("SELECT * FROM precio_oro WHERE fecha >= :dateLimitString ORDER BY fecha DESC")
    List<PrecioOro> getHistoryFromDate(String dateLimitString);

    @Query("DELETE FROM precio_oro WHERE id = :id")
    int deleteById(long id);
}
