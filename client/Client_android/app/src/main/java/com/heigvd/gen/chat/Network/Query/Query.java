package com.heigvd.gen.chat.Network.Query;

/**
 * Created by Antoine on 14.05.2016.
 */
abstract public class Query {
    protected transient String name;

    public Query(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
