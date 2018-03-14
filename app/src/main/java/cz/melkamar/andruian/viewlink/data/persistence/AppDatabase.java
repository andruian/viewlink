package cz.melkamar.andruian.viewlink.data.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import cz.melkamar.andruian.viewlink.model.datadef.ClassToLocPath;
import cz.melkamar.andruian.viewlink.model.datadef.DataDef;
import cz.melkamar.andruian.viewlink.model.datadef.SelectProperty;

@Database(entities = {
        DataDef.class,
        SelectProperty.class,
        ClassToLocPath.class
}, version = 7
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DataDefDao dataDefDao();
    public abstract SelectPropertyDao selectPropertyDao();
    public abstract ClassToLocPathDao classToLocPathDao();
}
