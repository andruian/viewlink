package cz.melkamar.andruian.viewlink;

import android.arch.persistence.room.Room;
import android.support.multidex.MultiDexApplication;

import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;


public class ViewLinkApplication extends MultiDexApplication {
//    private DaoSession daoSession;
//    @BindString(R.string.database_id) String DB_ID;

    private AppDatabase appDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydbname").fallbackToDestructiveMigration().build();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
