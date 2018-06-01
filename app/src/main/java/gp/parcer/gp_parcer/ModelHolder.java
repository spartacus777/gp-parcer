package gp.parcer.gp_parcer;

import java.util.concurrent.ConcurrentHashMap;

public class ModelHolder {

    private static ConcurrentHashMap<String, Model> models;

    public static void init(){
        models = new ConcurrentHashMap<>();
    }

    public static ConcurrentHashMap<String, Model> getModels() {
        return models;
    }
}
