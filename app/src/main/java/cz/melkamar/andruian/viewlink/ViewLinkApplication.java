package cz.melkamar.andruian.viewlink;

import android.arch.persistence.room.Room;
import android.support.multidex.MultiDexApplication;

import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;


public class ViewLinkApplication extends MultiDexApplication {
    public static final String DB_NAME = "cz.melkamar.andruian.viewlink.database";

    private AppDatabase appDatabase;
    @Override
    public void onCreate() {
        super.onCreate();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
