package com.ymkj.credit.server.utils;

import com.google.gson.Gson;


public class GsonUtils<D> {

    public static<D> D fromJson(String json,Class<D> d){
        Gson gson = new Gson();
        return gson.fromJson(json,d);
    }


    public static String toJson(Object o){
        Gson gson = new Gson();
        return gson.toJson(o);
    }

}
