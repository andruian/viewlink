package cz.melkamar.andruian.viewlink;

import android.app.Application;
import android.arch.persistence.room.Room;

import cz.melkamar.andruian.viewlink.data.persistence.AppDatabase;


public class ViewLinkApplication extends Application {
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
