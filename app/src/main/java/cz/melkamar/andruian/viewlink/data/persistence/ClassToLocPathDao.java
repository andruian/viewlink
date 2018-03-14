package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;

@Dao
public interface ClassToLocPathDao {
    @Query("SELECT * FROM classtolocpath WHERE parentDatadefUri=:dataDefUri")
    List<ClassToLocPath> getAllForDataDefUri(final String dataDefUri);

    @Query("SELECT * FROM classtolocpath WHERE parentDatadefUri=:dataDefUri AND forClassUri=:forClassUri")
    List<ClassToLocPath> getAllForDataDefUriAndClass(final String dataDefUri, final String forClassUri);

    @Insert
    void insertAll(ClassToLocPath... entities);
}
