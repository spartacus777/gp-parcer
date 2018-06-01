package gp.parcer.gp_parcer;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ModelHolder {

//    private static ConcurrentHashMap<String, Model> models;

    public static void init(){
//        models = new ConcurrentHashMap<>();
    }

//    public static ConcurrentHashMap<String, Model> getModels() {
//        return models;
//    }

    public static List<Model> getAllModels(){
        DaoSession daoSession = App.getDaoSession();
        ModelDao dao = daoSession.getModelDao();

        QueryBuilder<Model> queryBuilder = dao.queryBuilder();
        List<Model> list = queryBuilder.list();

        return list;
    }

    public static long getSize(){
        DaoSession daoSession = App.getDaoSession();
        ModelDao dao = daoSession.getModelDao();

        QueryBuilder<Model> queryBuilder = dao.queryBuilder();
        return queryBuilder.count();
    }

    public static boolean contains(String email){
        DaoSession daoSession = App.getDaoSession();
        ModelDao dao = daoSession.getModelDao();

        QueryBuilder<Model> queryBuilder = dao.queryBuilder().where(ModelDao.Properties.Email.eq(email));
        Model model = queryBuilder.unique();

        if (model != null){
            return true;
        } else {
            return false;
        }
    }

    public static void add(Model model){
        App.getDaoSession().getModelDao().insert(model);
    }
}
