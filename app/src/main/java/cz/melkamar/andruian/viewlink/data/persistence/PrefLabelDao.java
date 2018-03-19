package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.PrefLabel;

@Dao
public interface PrefLabelDao {
    @Query("SELECT * FROM PrefLabel WHERE parentDatadefUri=:dataDefUri")
    List<PrefLabel> getAllForDataDefUri(final String dataDefUri);

    @Insert
    void insertAll(PrefLabel... prefLabels);
}
