package com.faicalculer.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.faicalculer.model.Calculo;

import java.util.List;

@Dao
public interface CalculoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Calculo calculo);

    @Update
    int update(Calculo calculo);

    @Query("SELECT * FROM calculo ORDER BY id DESC")
    List<Calculo> getAllCalculos();

    @Query("DELETE FROM calculo WHERE id = :id")
    int deleteById(long id);
}
