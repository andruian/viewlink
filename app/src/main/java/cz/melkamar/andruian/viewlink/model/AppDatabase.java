package cz.melkamar.andruian.viewlink.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {
        DataDef.class
}, version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DataDefDao dataDefDao();
}
