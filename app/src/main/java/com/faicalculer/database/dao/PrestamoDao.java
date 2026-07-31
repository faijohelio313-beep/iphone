package com.faicalculer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.faicalculer.model.Prestamo;

import java.util.List;

@Dao
public interface PrestamoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Prestamo prestamo);

    @Update
    int update(Prestamo prestamo);

    @Query("SELECT * FROM prestamo WHERE id = :id LIMIT 1")
    Prestamo getById(long id);

    @Query("SELECT * FROM prestamo ORDER BY id DESC")
    List<Prestamo> getAllPrestamos();

    @Query("DELETE FROM prestamo WHERE id = :id")
    int deleteById(long id);
}
