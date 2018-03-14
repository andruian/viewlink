package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.DataDef;

@Dao
public interface DataDefDao {
    @Query("SELECT * FROM datadef")
    List<DataDef> getAll();

    @Insert
    void insertAll(DataDef... entities);

    @Delete
    void delete(DataDef dataDef);

    @Query("DELETE FROM DataDef")
    void deleteAll();
}
