package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.SelectProperty;

@Dao
public interface SelectPropertyDao {
    @Query("SELECT * FROM SelectProperty WHERE parentDatadefUri=:dataDefUri")
    List<SelectProperty> getAllForDataDefUri(final String dataDefUri);

    @Insert
    void insertAll(SelectProperty... entities);
}
