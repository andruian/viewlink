package cz.melkamar.andruian.viewlink;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import butterknife.BindString;
import cz.melkamar.andruian.viewlink.model.DaoMaster;
import cz.melkamar.andruian.viewlink.model.DaoSession;


public class ViewLinkApplication extends Application {
    private DaoSession daoSession;
    @BindString(R.string.database_id) String DB_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_ID);
        Database db = helper.getWritableDb();
        this.daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
