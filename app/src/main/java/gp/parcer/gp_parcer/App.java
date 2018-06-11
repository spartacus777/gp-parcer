package gp.parcer.gp_parcer;

import android.app.Application;
import android.content.Context;

import org.greenrobot.greendao.database.Database;

public class App extends Application {

    private static DaoSession daoSession;
    private static Context appContext;


    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        initGreenDao();
    }

    private static void initGreenDao(){
        DaoMaster.OpenHelper helper = new DatabaseUpgradeHelper(appContext, "serverconfigurations-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DaoSession getDaoSession(){
        return daoSession;
    }

    public static Context getContext(){
        return appContext;
    }
}
